package com.flyingpig.cloudmusic;

import com.flyingpig.cloudmusic.config.MvcConfig;
import com.flyingpig.feign.config.DefaultFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Import(MvcConfig.class)
@EnableScheduling
@EnableSwagger2
@EnableFeignClients(basePackages = "com.flyingpig.feign.clients", defaultConfiguration = DefaultFeignConfiguration.class)
@SpringBootApplication(scanBasePackages = "com.flyingpig")
public class MusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicApplication.class, args);
    }
}
