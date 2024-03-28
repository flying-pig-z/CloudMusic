package com.flyingpig.cloudmusic.controller;

import com.flyingpig.cloudmusic.constant.StatusCode;
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

import java.util.Objects;

import static com.flyingpig.cloudmusic.constant.RedisConstants.USER_LOGIN_KEY;

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
        try {
            System.out.println(user);
            return userService.login(user);
        } catch (RedisConnectionFailureException e) {
            return Result.error(StatusCode.SERVERERROR, "redis崩溃");
        } catch (Exception e) {
            System.out.println(e);
            return Result.error(StatusCode.SERVERERROR, "账号或密码错误，请重新登录");
        }
    }

    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result logout(@RequestHeader String Authorization) {
        String userId = JwtUtil.parseJwt(Authorization).getSubject();
        return userService.logout(userId);
    }

    @GetMapping("/whitelist")
    public boolean uuidIsInWhiteListOrNot(String userId, String uuid) {
        try {
            return Objects.equals(stringRedisTemplate.opsForValue().get(USER_LOGIN_KEY + userId), uuid);
        } catch (RedisConnectionFailureException e) {
            log.error("redis崩溃啦啦啦啦啦");
        }
        return true;
    }


}
