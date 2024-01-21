package com.flyingpig.feign.clients.fallback;

import com.flyingpig.feign.clients.UserClients;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClients> {

    @Override
    public UserClients create(Throwable throwable) {
        return new UserClients() {

            @Override
            public UserInfo selectUserInfoByUserId(Long userId) {
                log.error("查询用户信息异常");
                return new UserInfo();
            }

            @Override
            public boolean uuidIsInBlackListOrNot(String uuid) {
                log.error("查询黑名单异常");
                return false;
            }
        };
    }
}

