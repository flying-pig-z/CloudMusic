package com.flyingpig.feign.clients;

import com.flyingpig.feign.clients.fallback.UserClientFallbackFactory;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "auth-service",fallbackFactory = UserClientFallbackFactory.class)
public interface UserClients {
    @GetMapping("/users/user-info/{userId}")
    UserInfo selectUserInfoByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/users/blacklist")
    boolean uuidIsInBlackListOrNot(@RequestParam String uuid);
}
