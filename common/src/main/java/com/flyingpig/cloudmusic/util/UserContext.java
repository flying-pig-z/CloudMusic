package com.flyingpig.cloudmusic.util;

import com.flyingpig.cloudmusic.interceptor.LocalUserInfo;

public class UserContext {
    private static final ThreadLocal<LocalUserInfo> tl =new ThreadLocal<>();

    //保存当前登录用户信息到ThreadLocal
    public static void setUser(LocalUserInfo localUserInfo){
        tl.set(localUserInfo);
    }
    //获取当前登录的用户信息
    public static LocalUserInfo getUser(){
        return tl.get();
    }

    //移除当前登录用户信息
    public static void removeUser(){
        tl.remove();
    }
}
