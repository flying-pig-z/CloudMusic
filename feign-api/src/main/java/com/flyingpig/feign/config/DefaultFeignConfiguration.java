package com.flyingpig.feign.config;

import com.flyingpig.feign.clients.fallback.MusicClientFallbackFactory;
import com.flyingpig.feign.clients.fallback.UserClientFallbackFactory;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultFeignConfiguration {
    @Bean
    public Logger.Level logLevel(){
        return Logger.Level.BASIC;
    }
    @Bean
    public UserClientFallbackFactory userClientFallbackFactory(){
        return new UserClientFallbackFactory();
    }

    @Bean
    public MusicClientFallbackFactory musicClientFallbackFactory(){
        return new MusicClientFallbackFactory();
    }



}