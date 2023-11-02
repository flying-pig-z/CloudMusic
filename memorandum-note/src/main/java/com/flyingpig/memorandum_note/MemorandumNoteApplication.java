package com.flyingpig.memorandum_note;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSwagger2Doc
@SpringBootApplication
public class MemorandumNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemorandumNoteApplication.class, args);
    }

}
