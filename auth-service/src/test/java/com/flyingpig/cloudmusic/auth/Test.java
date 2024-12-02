package com.flyingpig.cloudmusic.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {

    public static void main(String[] args){
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        String encodeString=bCryptPasswordEncoder.encode("1");
        System.out.println(encodeString);
    }
}
