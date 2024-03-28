package com.flyingpig.cloudmusic.constant;

public class RedisConstants {

    public static final String USER_INFO_KEY="user:info:";

    public static final Long USER_INFO_TTL=30L;

    public static final String USER_LOGIN_KEY="user:login:";

    public static final Long USER_LOGIN_TTL = 30L;

    public static final String EMAIL_VERIFYCODE_KEY="email:verifycode:";

    public static final Long EMAIL_VERIFYCODE_TTL=120L;

    public static final String MUSIC_RANKLIST_KEY="music:rankList:";

    public static final String MUSIC_LIKE_KEY="music:like:";

    public static final Long MUSIC_LIKE_TTL=30L;

    public static final String MUSIC_LIKENUM_KEY="music:like-num:";

    public static final Long MUSIC_LIKENUM_TTL=30L;

    public static final String LIKE_LOCK_KEY = "lock:like:";

    public static final String DISLIKE_LOCK_KEY = "lock:dislike:";


}
