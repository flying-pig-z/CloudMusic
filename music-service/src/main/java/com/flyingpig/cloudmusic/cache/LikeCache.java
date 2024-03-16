package com.flyingpig.cloudmusic.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.dataobject.entity.Like;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import com.flyingpig.cloudmusic.mapper.LikeMapper;
import com.flyingpig.cloudmusic.mapper.MusicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.flyingpig.cloudmusic.constant.RedisConstants.*;
import static com.flyingpig.cloudmusic.constant.RedisConstants.MUSIC_LIKE_KEY;

@Slf4j
@Component
public class LikeCache {

    private final StringRedisTemplate stringRedisTemplate;

    public LikeCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    @Autowired
    MusicMapper musicMapper;

    @Autowired
    LikeMapper likeMapper;

    //如果返回点赞数，如果返回null代表歌曲不存在
    public Long getLikeNumByMusicId(long musicId) {
        //1.从redis查询用户信息缓存，并判断缓存是否存在
        String key = MUSIC_LIKENUM_KEY + musicId;
        String likeNum = stringRedisTemplate.opsForValue().get(key);
        if (likeNum != null) {
            //缓存存在直接返回
            return Long.parseLong(likeNum);
        }
        //2.缓存不存在，根据id查询数据库
        Music music = musicMapper.selectById(musicId);
        if (music != null) {
            //数据库存在则写入内存并返回
            stringRedisTemplate.opsForValue().set(key, music.getLikeNum().toString(), MUSIC_LIKENUM_TTL, TimeUnit.DAYS);
            return music.getLikeNum();
        } else {
            return null;
        }
    }

    public void likedataFromDbToRedis(Long musicId) {
        Music music = musicMapper.selectById(musicId);
        if (music != null && music.getLikeNum() > 0) {
            LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            likeLambdaQueryWrapper.eq(Like::getMusicId, musicId);
            List<Like> likeList = likeMapper.selectList(likeLambdaQueryWrapper);
            // 使用pipeline进行批处理优化
            RedisCallback<Object> pipelineCallback = (connection) -> {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                for (Like like : likeList) {
                    stringRedisConn.sAdd(MUSIC_LIKE_KEY + musicId, like.getUserId().toString());
                }
                // 设置集合的过期时间
                stringRedisConn.expire(MUSIC_LIKE_KEY + musicId, MUSIC_LIKE_TTL * 24 * 60 * 60); // 过期时间单位是秒
                return null;
            };
            stringRedisTemplate.executePipelined(pipelineCallback);
        }
    }


    public Boolean judgeLikeOrNotByMusicIdAndUserId(Long musicId,Long userId){
        Long likeNum = this.getLikeNumByMusicId(musicId);
        //歌曲不存在
        if (likeNum == null) {
            return false;
        }
        //判断对应的集合不存在但是有点赞，则数据过期，点赞数据则从db存入redis.
        boolean keyExist = stringRedisTemplate.hasKey(MUSIC_LIKE_KEY + musicId);
        if (!keyExist && likeNum != 0) {
            likedataFromDbToRedis(musicId);
        }

        //添加点赞数据
        return stringRedisTemplate.opsForSet().isMember(MUSIC_LIKE_KEY + musicId, userId.toString());
    }





}
