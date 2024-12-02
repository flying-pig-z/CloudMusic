package com.flyingpig.cloudmusic.auth.controller;


import com.flyingpig.cloudmusic.auth.service.UserService;
import com.flyingpig.cloudmusic.security.aop.BeforeAuthorize;
import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.cloudmusic.web.Result;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@Api("用户操作相关的api")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public Result selectUserInfoByUserId() {
        //封装完毕后调用service层的add方法
        return Result.success(userService.selectUserInfoByUserId(UserContext.getUser().getUserId()));
    }

    @PutMapping("/avatar")
    @ApiOperation("修改用户头像")
    public Result updateUserAvatar(@RequestParam MultipartFile avatar) throws IOException {
        //封装完毕后调用service层的add方法
        userService.updateAvatar(UserContext.getUser().getUserId(), avatar);
        return Result.success();
    }

    @PutMapping("/username")
    @ApiOperation("修改用户名")
    public Result updateUserName(String userName) {
        userService.updateUserName(UserContext.getUser().getUserId(), userName);
        return Result.success();
    }

    @ApiOperation("内部调用查询用户信息")
    @GetMapping("/user-info/{userId}")
    UserInfo selectUserInfoByUserId(@PathVariable("userId") Long userId) {
        return userService.selectUserInfoByUserId(userId);
    }

    @DeleteMapping
    @BeforeAuthorize(role = "admin")
    @ApiOperation("管理员删除用户")
    public Result deleteUser(Long userId) {
        userService.deleteUserById(userId);
        return Result.success();
    }


}