package com.flyingpig.cloudmusic.music;


import com.flyingpig.feign.config.DefaultFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableScheduling
@EnableSwagger2
@EnableFeignClients(basePackages = "com.flyingpig.feign.clients", defaultConfiguration = DefaultFeignConfiguration.class)
@SpringBootApplication
public class MusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicApplication.class, args);
    }
}
