package com.flyingpig.cloudmusic.musicrelated.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Like;
import com.flyingpig.cloudmusic.musicrelated.mapper.LikeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.flyingpig.cloudmusic.musicrelated.constant.RedisConstants.*;

@Slf4j
@Component
public class LikeCache {

    private final StringRedisTemplate stringRedisTemplate;

    public LikeCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Autowired
    LikeMapper likeMapper;

    public void likedataFromDbToRedis(String userId) {
        List<Like> likeList = likeMapper.selectList(new LambdaQueryWrapper<Like>()
                .eq(Like::getUserId, userId));
        // 如果为空返回空字符串
        if (likeList.isEmpty()) {
            return;
        }
        // 使用pipeline进行批处理优化
        RedisCallback<Object> pipelineCallback = (connection) -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            for (Like like : likeList) {
                stringRedisConn.sAdd(USER_MUSICLIKESET_KEY + userId, like.getMusicId().toString());
            }
            // 设置集合的过期时间
            stringRedisConn.expire(USER_MUSICLIKESET_KEY + userId, MUSIC_LIKE_TTL * 24 * 60 * 60); // 过期时间单位是秒
            return null;
        };
        stringRedisTemplate.executePipelined(pipelineCallback);
    }


}
