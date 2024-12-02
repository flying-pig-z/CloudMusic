package com.flyingpig.cloudmusic.auth.service;


import com.flyingpig.cloudmusic.auth.dataobject.entity.User;
import com.flyingpig.cloudmusic.web.Result;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {
    Result login(User user);

    Result logout(String userId);

    void addUser(User user);

    UserInfo selectUserInfoByUserId(Long userId);

    void updateUserName(Long userId, String userName);

    void updateAvatar(Long userId, MultipartFile avatarUrl);

    void deleteUserById(Long userId);

    void sendVerificationCode(String email);
}