package com.flyingpig.cloudmusic.auth.service;

import com.flyingpig.cloudmusic.auth.common.Result;
import com.flyingpig.cloudmusic.auth.dataobject.entity.User;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    Result login(User user);
    Result logout();

    void addUser(User user);

    UserInfo selectUserInfoByUserId(Long userId);
}