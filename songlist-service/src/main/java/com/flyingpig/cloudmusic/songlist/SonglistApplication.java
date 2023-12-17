package com.flyingpig.cloudmusic.songlist;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableSwagger2Doc
@SpringBootApplication
@EnableFeignClients(basePackages = "com.flyingpig.feign.clients")
public class SonglistApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonglistApplication.class, args);
    }

}
