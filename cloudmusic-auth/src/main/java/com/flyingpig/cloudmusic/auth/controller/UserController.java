package com.flyingpig.cloudmusic.auth.controller;

import com.flyingpig.cloudmusic.auth.common.Result;
import com.flyingpig.cloudmusic.auth.dataobject.entity.User;
import com.flyingpig.cloudmusic.auth.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth-service/user")
@Api("与用户表相关的api")
public class UserController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result login(@RequestBody User user) {
//        try {
            System.out.println(user);
            return loginService.login(user);
//        } catch (Exception e) {
//            return Result.error(2,"账号或密码错误，请重新登录");
//
//        }
    }

    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result logout() {
        return loginService.logout();
    }

}