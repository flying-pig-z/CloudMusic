package com.flyingpig.cloudmusic.auth.controller;


import com.flyingpig.cloudmusic.auth.constant.RedisConstants;
import com.flyingpig.cloudmusic.auth.dataobject.entity.User;
import com.flyingpig.cloudmusic.auth.dataobject.vo.EmailRegisterVO;
import com.flyingpig.cloudmusic.auth.service.UserService;
import com.flyingpig.cloudmusic.auth.util.EmailUtil;
import com.flyingpig.cloudmusic.web.Result;
import com.flyingpig.cloudmusic.web.StatusCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@Api("与邮件处理相关的api")
@Slf4j
public class MailController {
    @Autowired
    UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/verificationCode")
    @ApiOperation("用户获取验证码")
    public Result sendEmailVerificationCode(String email) {
        //检查email是否符合格式
        if (!EmailUtil.judgeEmailFormat(email)) {
            throw new RuntimeException("邮箱不符合格式");
        }
        userService.sendVerificationCode(email);
        return Result.success("验证码已发送");
    }


    @PostMapping("/register")
    @ApiOperation("通过验证码完成注册")
    public Result emailRegister(@RequestBody EmailRegisterVO emailRegisterVO) {
        String verificationCode = stringRedisTemplate.opsForValue().get(RedisConstants.EMAIL_VERIFYCODE_KEY + emailRegisterVO.getEmail());
        log.info("邮箱{}请求验证码{}",emailRegisterVO.getEmail(),verificationCode);
        if (verificationCode != null && verificationCode.equals(emailRegisterVO.getVerificationCode())) {
            // 添加用户
            userService.addUser(new User().setRole("user")
                    .setUsername(emailRegisterVO.getUsername())
                    .setPassword(passwordEncoder.encode(emailRegisterVO.getPassword()))
                    .setEmail(emailRegisterVO.getEmail()));
            return Result.success("添加成功,请联系管理员审核");
        } else {
            throw new RuntimeException("验证码验证错误");
        }

    }
}
