package com.flyingpig.cloudmusic.songlist;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSwagger2Doc
@SpringBootApplication
public class SonglistApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonglistApplication.class, args);
    }

}
