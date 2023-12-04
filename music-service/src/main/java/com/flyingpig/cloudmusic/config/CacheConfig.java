package com.flyingpig.cloudmusic.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // 配置具体的缓存管理器，例如ConcurrentMapCacheManager, EhCacheCacheManager等
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        Cache musicCache = new ConcurrentMapCache("musicCache");
        cacheManager.setCaches(Arrays.asList(musicCache));
        return cacheManager;
    }
}
