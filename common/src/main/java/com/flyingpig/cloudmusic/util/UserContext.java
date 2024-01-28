package com.flyingpig.cloudmusic.util;

public class UserContext {
    private static final ThreadLocal<Long> tl =new ThreadLocal<>();

    //保存当前登录用户信息到ThreadLocal
    public static void setUser(Long userId){
        tl.set(userId);
    }
    //获取当前登录的用户信息
    public static Long getUser(){
        return tl.get();
    }

    //移除当前登录用户信息
    public static void removeUser(){
        tl.remove();
    }
}
