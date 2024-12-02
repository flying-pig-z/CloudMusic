package com.flyingpig.cloudmusic.musicrelated.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.cache.StringCacheUtil;
import com.flyingpig.cloudmusic.musicrelated.cache.LikeCache;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Like;
import com.flyingpig.cloudmusic.musicrelated.dataobject.message.LikeMessage;
import com.flyingpig.cloudmusic.musicrelated.mapper.LikeMapper;
import com.flyingpig.cloudmusic.musicrelated.service.LikeService;
import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.feign.clients.MusicClients;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import com.flyingpig.feign.dataobject.dto.UserLikeInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.flyingpig.cloudmusic.musicrelated.constant.MQConstants.MUSIC_LIKE_EXCHANGE_NAME;
import static com.flyingpig.cloudmusic.musicrelated.constant.RedisConstants.*;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class LikeServiceImpl implements LikeService {
    @Autowired
    LikeMapper likeMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    LikeCache likeCache;

    @Autowired
    StringCacheUtil stringCacheUtil;

    @Autowired
    MusicClients musicClients;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public boolean likeMusic(Like like) {
        String userId = like.getUserId().toString();
        String musicId = like.getMusicId().toString();

        // 创建锁的名称，可以根据需要调整
        RLock lock = redissonClient.getLock(MUSIC_LIKELOCK_KEY + userId);

        try {
            // 获取锁并设置超时，防止死锁
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                // 判断缓存中是否存在用户的点赞集合
                if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(USER_MUSICLIKESET_KEY + userId))) {
                    // 同步数据库中的点赞数据到缓存
                    stringRedisTemplate.opsForValue().set(MUSIC_LIKENUM_KEY + musicId,
                            likeMapper.selectCount(
                                    new LambdaQueryWrapper<Like>().eq(Like::getMusicId, musicId)).toString());
                    likeCache.likedataFromDbToRedis(userId);
                }

                // 缓存同步后，检查是否已经点赞
                if (!Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(USER_MUSICLIKESET_KEY + userId, musicId))) {
                    // 未点赞，进行点赞操作
                    stringRedisTemplate.opsForSet().add(USER_MUSICLIKESET_KEY + userId, musicId);
                    stringRedisTemplate.opsForValue().increment(MUSIC_LIKENUM_KEY + musicId, 1);

                    // 发送点赞消息到RabbitMQ异步更新数据库
                    rabbitTemplate.convertAndSend(MUSIC_LIKE_EXCHANGE_NAME,
                            String.valueOf(Long.parseLong(musicId) % 2),
                            new LikeMessage(musicId, userId, LikeMessage.INCREASE));
                    return true;
                } else {
                    // 已点赞，进行取消点赞操作
                    stringRedisTemplate.opsForSet().remove(USER_MUSICLIKESET_KEY + userId, musicId);
                    stringRedisTemplate.opsForValue().decrement(MUSIC_LIKENUM_KEY + musicId, 1);

                    // 发送取消点赞消息到RabbitMQ异步更新数据库
                    rabbitTemplate.convertAndSend(MUSIC_LIKE_EXCHANGE_NAME,
                            String.valueOf(Long.parseLong(musicId) % 2),
                            new LikeMessage(musicId, userId, LikeMessage.DECREASE));
                    return false;
                }
            } else {
                // 如果获取锁失败，可能是其他线程正在处理，返回失败或重试
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    @Override
    public UserLikeInfo getLikeInfoByUserIdAndMusicId(Long userId, Long musicId) {
        UserLikeInfo userLikeInfo = new UserLikeInfo();

        // 获取并缓存音乐点赞数
        userLikeInfo.setMusicLikeNum(stringCacheUtil.safeGetWithLock(
                MUSIC_LIKENUM_KEY + musicId, Long.class,
                () -> (long) likeMapper.selectCount(new LambdaQueryWrapper<Like>().eq(Like::getMusicId, musicId)),
                MUSIC_LIKE_TTL, TimeUnit.SECONDS));

        String userLikeSetKey = USER_MUSICLIKESET_KEY + userId;

        // 检查用户点赞集合是否存在于缓存中，不存在则从数据库同步数据到缓存
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(userLikeSetKey))) {
            likeCache.likedataFromDbToRedis(userId.toString());
        }

        // 检查是否已经点赞，将 musicId 转为 String
        boolean isLiked = Boolean.TRUE.equals(
                stringRedisTemplate.opsForSet().isMember(userLikeSetKey, musicId.toString()));

        userLikeInfo.setLikeOrNot(isLiked);

        return userLikeInfo;
    }

    @Override
    public List<MusicDetail> listLikeByUserId() {
        // 判断缓存中是否存在用户的点赞集合
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(USER_MUSICLIKESET_KEY + UserContext.getUser().getUserId()))) {
            // 同步数据库中的点赞数据到缓存
            likeCache.likedataFromDbToRedis(UserContext.getUser().getUserId().toString());
        }
        String userLikeSetKey = USER_MUSICLIKESET_KEY + UserContext.getUser().getUserId();
        Set<String> resultSet = stringRedisTemplate.opsForSet().members(userLikeSetKey);
        List<MusicDetail> musicDetails = new ArrayList<>();
        for (String musicId : resultSet) {
            musicDetails.add(musicClients.selectMusicDetailById(Long.parseLong(musicId)));
        }
        return musicDetails;
    }


}
