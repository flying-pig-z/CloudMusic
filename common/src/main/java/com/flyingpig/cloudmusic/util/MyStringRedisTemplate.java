package com.flyingpig.cloudmusic.util;

import cn.hutool.core.util.BooleanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;



@Slf4j
@Component
public class MyStringRedisTemplate {
    private final StringRedisTemplate stringRedisTemplate;
    private static final String ID_PREFIX = UUID.randomUUID() + "-";

    public MyStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public Boolean delete(String key) {
        try {
            return stringRedisTemplate.delete(key);
        } catch (RedisConnectionFailureException e) {
            log.error("redis崩溃啦啦啦啦啦");
        }
        return false;
    }


    public void set(String key, String jsonStr, Long ttl, TimeUnit timeUnit) {
        try {
            stringRedisTemplate.opsForValue().set(key, jsonStr, ttl, timeUnit);
        } catch (RedisConnectionFailureException e) {
            log.error("redis崩溃啦啦啦啦啦");
        }
    }

    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (RedisConnectionFailureException e) {
            log.error("redis崩溃啦啦啦啦啦");
        }
        return null;
    }


    public boolean tryLock(String key) {

        // 获取线程标示
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        Boolean flag = true;
        try {
            // 获取锁
            flag = stringRedisTemplate.opsForValue().setIfAbsent(key , threadId, 10, TimeUnit.SECONDS);
        } catch (RedisConnectionFailureException e) {
            log.error("redis崩溃啦啦啦啦啦");
        }
        return BooleanUtil.isTrue(flag);
    }

    public void unlock(String key) {
        // 获取线程标示
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        try {
            // 获取锁中的标示
            String id = stringRedisTemplate.opsForValue().get(key);
            // 判断标示是否一致
            if(threadId.equals(id)) {
                // 释放锁
                stringRedisTemplate.delete(key);
            }
        } catch (RedisConnectionFailureException e) {
            log.error("redis崩溃啦啦啦啦啦");
        }

    }


}
