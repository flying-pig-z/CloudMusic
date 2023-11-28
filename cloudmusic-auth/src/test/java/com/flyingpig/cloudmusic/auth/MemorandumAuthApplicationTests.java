package com.flyingpig.cloudmusic.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.util.Scanner;

@SpringBootTest
class MemorandumAuthApplicationTests {
    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;
    public static void main(String[] args){
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        String encodeString=bCryptPasswordEncoder.encode("102201604");
        System.out.println(encodeString);
    }

}
