package com.flyingpig.cloudmusic.util;

public class RedisConstants {

    public static final String USER_INFO_KEY="user:info:";

    public static final Long USER_INFO_TTL=30L;

    public static final String USER_BLACKLIST_KEY="user:blacklist:";

    public static final Long USER_BLACKLIST_TTL = 30L;

    public static final String EMAIL_VERIFYCODE_KEY="email:verifycode:";

    public static final Long EMAIL_VERIFYCODE_TTL=120L;

    public static final String MUSIC_RANKLIST_KEY="music:rankList:";


}
