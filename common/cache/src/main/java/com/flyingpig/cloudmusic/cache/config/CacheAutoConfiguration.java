package com.flyingpig.cloudmusic.cache.config;

import com.flyingpig.cloudmusic.cache.StringCacheUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheAutoConfiguration {
    @Bean
    public StringCacheUtil stringCacheUtil() {
        return new StringCacheUtil();
    }
}
