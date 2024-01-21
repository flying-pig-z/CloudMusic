package com.flyingpig.cloudmusic.controller;

import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.UserService;
import com.flyingpig.cloudmusic.util.AliOSSUtils;
import com.flyingpig.cloudmusic.util.JwtUtil;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import io.jsonwebtoken.Claims;
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

    @Autowired
    private AliOSSUtils aliOSSUtils;



    @GetMapping("/user-info/{userId}")
    UserInfo selectUserInfoByUserId(@PathVariable("userId") Long userId){
        return userService.selectUserInfoByUserId(userId);
    }


    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public Result selectUserInfoByUserId(@RequestHeader String Authorization){
        //封装完毕后调用service层的add方法
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        return Result.success(userService.selectUserInfoByUserId(userId));
    }
    @PutMapping("/avatar")
    @ApiOperation("修改用户头像")
    public Result updateUserAvatar(@RequestHeader String Authorization, @RequestParam MultipartFile avatar) throws IOException {
        //封装完毕后调用service层的add方法
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        String avatarUrl = aliOSSUtils.upload(avatar);
        userService.updateAvatar(userId,avatarUrl);
        return Result.success();
    }

    @PutMapping("/username")
    @ApiOperation("修改用户名")
    public Result updateUserName(@RequestHeader String Authorization,@RequestParam String userName){
        Claims claims = JwtUtil.parseJwt(Authorization);
        Long userId = Long.parseLong(claims.getSubject());
        userService.updateUserName(userId,userName);
        return Result.success();
    }

}