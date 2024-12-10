package com.flyingpig.cloudmusic.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyingpig.cloudmusic.auth.constant.RedisConstants;
import com.flyingpig.cloudmusic.auth.dataobject.entity.User;
import com.flyingpig.cloudmusic.auth.mapper.UserMapper;
import com.flyingpig.cloudmusic.auth.service.UserService;
import com.flyingpig.cloudmusic.auth.util.EmailUtil;
import com.flyingpig.cloudmusic.cache.StringCacheUtil;
import com.flyingpig.cloudmusic.auth.dataobject.dto.LoginUser;
import com.flyingpig.cloudmusic.file.AliOSSUtils;
import com.flyingpig.cloudmusic.security.util.JwtUtil;
import com.flyingpig.cloudmusic.web.Result;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    StringCacheUtil myStringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    AliOSSUtils aliOSSUtils;

    @Value("${spring.mail.username}")
    private String emailUserName;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JavaMailSenderImpl mailSender;

    @Override
    public Result login(User user) {
        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        //如果认证没通过，给出对应的提示
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("账号或密码错误，请重新登录");
        }

        //如果认证通过了，使用userid生成一个jwt jwt存入ResponseResult返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userid = loginUser.getUser().getId().toString();
        String uuid = JwtUtil.getUUID();
        String jwt = JwtUtil.createJWT(userid, JwtUtil.JWT_TTL, uuid);

        //将jwt存入redis中，键为userId,值为token的uuid，不直接存jwt可以节省缓存
        myStringRedisTemplate.set(RedisConstants.USER_LOGIN_KEY + userid, uuid, RedisConstants.USER_LOGIN_TTL, TimeUnit.DAYS);

        //返回
        Map<String, Object> map = new HashMap<>();
        map.put("token", jwt);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", user.getEmail());
        User selectuser = userMapper.selectOne(userQueryWrapper);
        map.put("email", selectuser.getEmail());
        map.put("id", selectuser.getId());
        return Result.success(map);
    }

    @Override
    public Result logout(String userId) {
        //将token加入黑名单
        myStringRedisTemplate.delete(RedisConstants.USER_LOGIN_KEY + userId);
        return new Result(200, "退出成功", null);
    }

    @Override
    public void addUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public UserInfo selectUserInfoByUserId(Long userId) {
        return myStringRedisTemplate.safeGetWithLock(RedisConstants.USER_INFO_KEY + userId, UserInfo.class, () -> {
            UserInfo result = new UserInfo();
            User user = userMapper.selectById(userId);
            if (user == null) {
                return null;
            }
            BeanUtils.copyProperties(user, result);
            return result;
        }, RedisConstants.USER_INFO_TTL, TimeUnit.DAYS);
    }

    @Override
    public void updateUserName(Long userId, String userName) {
        User user = new User();
        user.setId(userId);
        user.setUsername(userName);
        userMapper.updateById(user);
        //删除缓存，防止数据不一致
        myStringRedisTemplate.delete(RedisConstants.USER_INFO_KEY + userId);

    }

    @Override
    public void updateAvatar(Long userId, MultipartFile avatarUrl) {
        try {
            // 删除原来的头像
            aliOSSUtils.deleteFileByUrl(userMapper.selectById(userId).getAvatar());
            // 更新现有的头像
            userMapper.updateById(new User().setId(userId)
                    .setAvatar(aliOSSUtils.upload(avatarUrl)));
            // 删除缓存，防止数据不一致
            myStringRedisTemplate.delete(RedisConstants.USER_INFO_KEY + userId);
        } catch (Exception e) {
            throw new RuntimeException("修改头像异常" + e.getMessage());
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        userMapper.deleteById(userId);
    }

    @Override
    @Async
    public void sendVerificationCode(String email) {
        // 验证码 邮件主题 邮件正文
        String verificationCode = EmailUtil.createVerificationCode();
        String subject = "【喵听】验证码";
        String text = String.format("【喵听】验证码：%s，您正在申请注册喵听账号" +
                "（若非本人操作，请删除本邮件）", verificationCode);

        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailUserName);  // 设置发件邮箱
        message.setTo(email);            // 设置收件邮箱
        message.setSubject(subject);     // 设置邮件主题
        message.setText(text);           // 设置邮件正文
        mailSender.send(message);

        // 存入缓存
        stringRedisTemplate.opsForValue().set(RedisConstants.EMAIL_VERIFYCODE_KEY + email, verificationCode, RedisConstants.EMAIL_VERIFYCODE_TTL, TimeUnit.SECONDS);
    }


}