package com.flyingpig.cloudmusic.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusPage {
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new  PaginationInterceptor();
    }
}