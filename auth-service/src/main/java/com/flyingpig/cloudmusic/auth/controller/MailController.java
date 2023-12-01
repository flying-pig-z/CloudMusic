package com.flyingpig.cloudmusic.auth.controller;

import com.flyingpig.cloudmusic.auth.common.Result;
import com.flyingpig.cloudmusic.auth.dataobject.entity.User;
import com.flyingpig.cloudmusic.auth.dataobject.vo.EmailRegisterVO;
import com.flyingpig.cloudmusic.auth.service.LoginService;
import com.flyingpig.cloudmusic.auth.util.EmailUtil;
import com.flyingpig.cloudmusic.auth.util.RedisCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/email")
@Api("与邮件处理相关的api")
public class MailController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private JavaMailSenderImpl mailSender;
    //这里要使用工具类，不然各个方法之间的redis数据无法共用
    @Autowired
    private RedisCache redisCache;
    @Value("${spring.mail.username}")
    private String emailUserName;

    @GetMapping("/verificationCode")
    @ApiOperation("用户获取验证码")
    public Result sendEmailVerificationCode(@RequestParam String email) {
        //检查email是否符合格式
        if(!EmailUtil.judgeEmailFormat(email)){
            return Result.error(2,"邮箱不符合格式");
        }
        //检查redis中有无验证码，如果有，则返回已存在
        String verificationCode=redisCache.getCacheObject(email);
        if(verificationCode!=null){
            return Result.error(2,"发送过于频繁，请一分钟后再试");
        }else {
            //如果没有，生成验证码存入缓存并发送
            verificationCode = EmailUtil.createVerificationCode();
            //发送
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailUserName);//设置发件qq邮箱
            message.setTo(email);      // 设置收件人邮箱地址
            message.setSubject("验证码");    // 设置邮件主题
            message.setText(verificationCode);   // 设置邮件正文
            mailSender.send(message);
            //存入缓存
            redisCache.setCacheObject(email, verificationCode, 60, TimeUnit.SECONDS);
            return Result.success("验证码已发送");
        }
    }

    @PostMapping("/register")
    @ApiOperation("通过验证码完成注册")
    public Result emailRegister(@RequestBody EmailRegisterVO emailRegisterVO) {
        System.out.println(emailRegisterVO.getEmail());
        String verificationCode=redisCache.getCacheObject(emailRegisterVO.getEmail());
        System.out.println(verificationCode);
        if(verificationCode!=null&&verificationCode.equals(emailRegisterVO.getVerificationCode())){
            //添加用户
            User user=new User();
            user.setUsername(emailRegisterVO.getUsername());
            user.setPassword(emailRegisterVO.getPassword());
            user.setEmail(emailRegisterVO.getEmail());
            loginService.addUser(user);
            return Result.success("添加成功,请联系管理员审核");
        }else {
            return Result.error(2,"验证码验证错误");
        }

    }
}
