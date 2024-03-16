package com.flyingpig.cloudmusic.controller;

import com.flyingpig.cloudmusic.dataobject.entity.User;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.UserService;
import com.flyingpig.cloudmusic.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import static com.flyingpig.cloudmusic.constant.RedisConstants.USER_BLACKLIST_KEY;

@RestController
@RequestMapping("/users")
@Api("用户操作相关的api")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result login(@RequestBody User user) {
        System.out.println(user);
        return userService.login(user);
    }

    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result logout(@RequestHeader String Authorization) {
        String uuid = JwtUtil.getUUIDFromJWT(Authorization);
        return userService.logout(uuid);
    }

    @GetMapping("/blacklist")
    public boolean uuidIsInBlackListOrNot(@RequestParam String uuid) {
        try {
            return stringRedisTemplate.opsForValue().get(USER_BLACKLIST_KEY + uuid) != null;
        } catch (RedisConnectionFailureException e) {
            log.error("redis崩溃啦啦啦啦啦");
        }
        return true;
    }


}
