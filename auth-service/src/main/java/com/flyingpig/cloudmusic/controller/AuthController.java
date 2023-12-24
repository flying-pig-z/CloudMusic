package com.flyingpig.cloudmusic.controller;

import com.flyingpig.cloudmusic.dataobject.entity.User;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.UserService;
import com.flyingpig.cloudmusic.util.JwtUtil;
import com.flyingpig.cloudmusic.util.RedisCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Api("用户操作相关的api")
public class AuthController {
    @Autowired
    RedisCache redisCache;

    @Autowired
    private UserService userService;

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
    public boolean uuidIsInBlackListOrNot(@RequestParam String uuid){
        return redisCache.getCacheObject("blacklist:" + uuid) != null;
    }


}
