package com.flyingpig.cloudmusic.musicrelated;


import com.flyingpig.feign.config.DefaultFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableFeignClients(basePackages = "com.flyingpig.feign.clients", defaultConfiguration = DefaultFeignConfiguration.class)
@SpringBootApplication
public class MusicRelatedServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicRelatedServiceApplication.class, args);
    }

}
