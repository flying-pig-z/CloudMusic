package com.flyingpig.cloudmusic.auth.controller;

import com.flyingpig.cloudmusic.auth.constant.RedisConstants;
import com.flyingpig.cloudmusic.auth.dataobject.entity.User;
import com.flyingpig.cloudmusic.security.util.JwtUtil;
import com.flyingpig.cloudmusic.auth.service.UserService;
import com.flyingpig.cloudmusic.web.Result;
import com.flyingpig.cloudmusic.web.StatusCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
            String redisValue = stringRedisTemplate.opsForValue().get(RedisConstants.USER_LOGIN_KEY + userId);
            // 去掉引号和空格后再进行比较
            if (redisValue != null) {
                redisValue = redisValue.replace("\"", "").trim();
            }
            return Objects.equals(redisValue, uuid);
        } catch (RedisConnectionFailureException e) {
            log.error("redis崩溃啦啦啦啦啦");
        }
        return true;
    }



}
