package com.flyingpig.feign.clients;

import com.flyingpig.feign.dataobject.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("auth-service")
@RequestMapping("/users")
public interface UserClients {
    @GetMapping("/user-info/{userId}")
    UserInfo selectUserInfoByUserId(@PathVariable("userId") Long userId);
}
