package com.flyingpig.cloudmusic.service;


import com.flyingpig.cloudmusic.dataobject.entity.User;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import org.springframework.stereotype.Service;


public interface UserService {
    Result login(User user);

    Result logout(String userId);

    void addUser(User user);

    UserInfo selectUserInfoByUserId(Long userId);

    void updateUserName(Long userId, String userName);

    void updateAvatar(Long userId, String avatarUrl);

    void deleteUserById(Long userId);
}