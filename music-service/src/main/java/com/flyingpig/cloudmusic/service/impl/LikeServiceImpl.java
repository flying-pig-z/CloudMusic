package com.flyingpig.cloudmusic.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.dataobject.entity.Like;
import com.flyingpig.cloudmusic.dataobject.message.LikeMessage;
import com.flyingpig.cloudmusic.mapper.LikeMapper;
import com.flyingpig.cloudmusic.mapper.MusicMapper;
import com.flyingpig.cloudmusic.service.LikeService;
import com.flyingpig.cloudmusic.cache.LikeCache;
import com.flyingpig.cloudmusic.util.MyStringRedisTemplate;
import com.flyingpig.cloudmusic.util.RedissionUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.flyingpig.cloudmusic.util.RabbitMQConstants.*;
import static com.flyingpig.cloudmusic.util.RedisConstants.*;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class LikeServiceImpl implements LikeService {
    @Autowired
    LikeMapper likeMapper;
    @Autowired
    MusicMapper musicMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    LikeCache likeCache;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RedissionUtil redissionUtil;

    @Override
    public boolean likeMusic(Like like) {
        Long userId = like.getUserId();
        Long musicId = like.getMusicId();
        //未点赞，加锁缓存点赞并异步数据库点赞
        RLock lock = redissonClient.getLock(LIKE_LOCK_KEY + musicId);
        //判断获取锁成功
        if (redissionUtil.tryLockWithRedisson(lock, 10, TimeUnit.SECONDS)) {
            try {
                Long likeNum = likeCache.getLikeNumByMusicId(musicId);
                //歌曲不存在
                if (likeNum == null) {
                    return false;
                }

                //判断对应的集合不存在但是有点赞，则数据过期，点赞数据则从db存入redis.
                boolean keyExist = stringRedisTemplate.hasKey(MUSIC_LIKE_KEY + musicId);
                if (!keyExist && likeNum != 0) {
                    likeCache.likedataFromDbToRedis(musicId);
                }

                //更新点赞数据
                Boolean isMember = stringRedisTemplate.opsForSet().isMember(MUSIC_LIKE_KEY + musicId, userId.toString());
                if (BooleanUtil.isFalse(isMember)) {
                    //点赞
                    Long nowLikeNum = likeNum + 1;
                    stringRedisTemplate.opsForSet().add(MUSIC_LIKE_KEY + musicId, userId.toString());
                    stringRedisTemplate.opsForValue().set(MUSIC_LIKENUM_KEY + musicId, nowLikeNum.toString());
                    // 将文件信息发送到RabbitMQ队列中,使用routingKey使得同一个音乐点赞数据进入同一个队列，避免并发问题
                    rabbitTemplate.convertAndSend(MUSIC_LIKE_EXCHANGE_NAME, String.valueOf(musicId % 2), new LikeMessage(musicId, userId, nowLikeNum));
                } else {
                    //取消点赞
                    Long nowLikeNum = likeNum - 1;
                    stringRedisTemplate.opsForSet().remove(MUSIC_LIKE_KEY + musicId, userId.toString());
                    stringRedisTemplate.opsForValue().set(MUSIC_LIKENUM_KEY + musicId, nowLikeNum.toString());
                    // 将文件信息发送到RabbitMQ队列中
                    rabbitTemplate.convertAndSend(MUSIC_DISLIKE_EXCHANGE_NAME, String.valueOf(musicId % 2), new LikeMessage(musicId, userId, nowLikeNum));
                }
            } finally {
                //释放锁
                lock.unlock();
            }
        }
        return true;
    }


}
