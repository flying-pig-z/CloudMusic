package com.flyingpig.cloudmusic;

import com.flyingpig.cloudmusic.config.MvcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Import(MvcConfig.class)
@EnableSwagger2
@SpringBootApplication(scanBasePackages = "com.flyingpig")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
