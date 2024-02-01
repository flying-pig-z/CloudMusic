package com.flyingpig.cloudmusic.util;

import cn.hutool.core.util.BooleanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class MyStringRedisTemplate {
    private final StringRedisTemplate stringRedisTemplate;
    public MyStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public Boolean delete(String key) {
        try {
            return stringRedisTemplate.delete(key);
        }catch (RedisConnectionFailureException e){
            log.error("redis崩溃啦啦啦啦啦");
        }
        return false;
    }


    public void set(String key, String jsonStr, Long ttl, TimeUnit timeUnit) {
        try{
            stringRedisTemplate.opsForValue().set(key, jsonStr ,ttl, timeUnit);
        }catch (RedisConnectionFailureException e){
            log.error("redis崩溃啦啦啦啦啦");
        }
    }

    public String get(String key) {
        try{
            return stringRedisTemplate.opsForValue().get(key);
        }catch (RedisConnectionFailureException e){
            log.error("redis崩溃啦啦啦啦啦");
        }
        return null;
    }


    public boolean tryLock(String key) {
        Boolean flag = true;
        try {
            flag=stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        }catch (RedisConnectionFailureException e){
            log.error("redis崩溃啦啦啦啦啦");
        }
        return BooleanUtil.isTrue(flag);
    }

    public void unlock(String key) {
        try {stringRedisTemplate.delete(key);stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        }catch (RedisConnectionFailureException e){
            log.error("redis崩溃啦啦啦啦啦");
        }

    }
}
