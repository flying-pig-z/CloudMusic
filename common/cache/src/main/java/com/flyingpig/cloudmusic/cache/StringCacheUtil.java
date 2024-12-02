package com.flyingpig.cloudmusic.cache;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;


public class StringCacheUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    // 加入缓存
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), time, unit);
    }


    // 普通查询
    public <T> T get(String key, Class<T> type) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (String.class.isAssignableFrom(type)) {
            return (T) value;
        }
        return JSON.parseObject(value, type);
    }


    // 查询时候缓存空值防止缓存穿透,互斥锁查询防止缓存击穿
    public <T> T safeGetWithLock(
            String key, Class<T> type, CacheLoader<T> cacheLoader, Long time, TimeUnit unit) {
        // 从redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        // 1.命中且不为空字符串，直接返回;命中却为空字符串，返回null
        if (StrUtil.isNotBlank(json)) {
            System.out.println(666);
            return JSON.parseObject(json, type);
        } else if (json != null) {
            return null;
        }
        // 2.没有命中，去数据库查询，查到写入数据库，没查到则缓存空字符串

        // 获取锁
        String lockKey = "lock:" + key;
        T result = null;
        RLock rLock = redissonClient.getLock(lockKey);
        rLock.lock();
        try {
            // 再次查询redis，双重判定
            if (StrUtil.isNotBlank(json)) {
                return JSON.parseObject(json, type);
            } else if (json != null) {
                return null;
            }
            // 获取锁成功，查询数据库。存在，写入redis;不存在，将空值写入redis，返回null。
            result = loadAndSet(key, cacheLoader, time, unit);
        } finally {
            // 释放锁
            rLock.unlock();
        }
        // 返回
        return result;
    }


    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    private <T> T loadAndSet(String key, CacheLoader<T> cacheLoader, Long time, TimeUnit unit) {
        // 获取锁成功，查询数据库
        T result = cacheLoader.load();
        // 不存在，将空值写入redis，返回null
        if (result == null) {
            stringRedisTemplate.opsForValue().set(key, "", time, TimeUnit.MINUTES);
        }
        // 存在，写入redis
        this.set(key, result, time, unit);
        return result;
    }


}
