package com.flyingpig.cloudmusic.file.config;

import com.flyingpig.cloudmusic.file.AliOSSUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoFileConfiguration {
    @Bean
    public AliOSSUtils aliOSSUtils() {
        return new AliOSSUtils();
    }
}
