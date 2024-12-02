package com.flyingpig.cloudmusic.auth.constant;

public class RedisConstants {

    public static final String USER_INFO_KEY="user:info:";

    public static final Long USER_INFO_TTL=30L;

    public static final String USER_LOGIN_KEY="user:login:";

    public static final Long USER_LOGIN_TTL = 30L;

    public static final String EMAIL_VERIFYCODE_KEY="email:verifycode:";

    public static final Long EMAIL_VERIFYCODE_TTL=120L;
}
