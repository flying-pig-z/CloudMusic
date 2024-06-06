package com.flyingpig.cloudmusic.util.cache;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.flyingpig.cloudmusic.constant.RedisConstants.CACHE_NULL_TTL;


@Slf4j
@Component
public class MyStringRedisTemplate {
    private final StringRedisTemplate stringRedisTemplate;

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    public MyStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


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
        // 1.从redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(json)) {
            // 3.存在，直接返回
            return JSON.parseObject(json, type);
        }
        // 判断命中的是否是空值
        if (json != null) {
            // 返回一个错误信息
            return null;
        }
        // 4.实现缓存重建
        // 4.1.获取互斥锁
        String lockKey = "lock:" + key;
        T t;
        try {
            boolean isLock = tryLock(lockKey, 10);
            // 4.2.判断是否获取成功
            if (!isLock) {
                // 4.3.获取锁失败，休眠并重试
                Thread.sleep(50);
                return safeGetWithLock(key, type, cacheLoader, time, unit);
            }
            // 4.4.获取锁成功，根据id查询数据库
            t = cacheLoader.load();
            // 5.不存在，返回错误
            if (t == null) {
                // 将空值写入redis
                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                // 返回错误信息
                return null;
            }
            // 6.存在，写入redis
            this.set(key, t, time, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            // 7.释放锁
            unlock(lockKey);
        }
        // 8.返回
        return t;
    }




    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }





    public boolean tryLock(String lockKey, long timeoutSec) {
        // 获取线程标示
        String threadId = Thread.currentThread().getId()+"";
        // 获取锁并判断是否成功
        // setIfAbsent方法是原子的，只有一个线程能够获取到锁，对应redis的setNx命令
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(lockKey, threadId, timeoutSec, TimeUnit.SECONDS));
    }

    public void unlock(String lockKey) {
        //通过del删除锁
        stringRedisTemplate.delete(lockKey);
    }


}
