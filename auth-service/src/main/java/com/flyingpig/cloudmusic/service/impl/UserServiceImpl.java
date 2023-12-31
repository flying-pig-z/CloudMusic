package com.flyingpig.cloudmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyingpig.cloudmusic.dataobject.dto.LoginUser;
import com.flyingpig.cloudmusic.dataobject.entity.User;
import com.flyingpig.cloudmusic.mapper.UserMapper;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.UserService;
import com.flyingpig.cloudmusic.util.RedisCache;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.flyingpig.cloudmusic.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    RedisCache redisCache;

    @Autowired
    private UserMapper userMapper;
    @Override
    public Result login(User user) {
        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("登录失败");
        }
        //如果认证通过了，使用userid生成一个jwt jwt存入ResponseResult返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userid = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userid);
        //把完整的用户信息存入redis  userid作为key
//        redisCache.setCacheObject("login:"+userid,loginUser);
        Map<String,Object> map = new HashMap<>();
        map.put("token",jwt);
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq("email",user.getEmail());
        User selectuser=userMapper.selectOne(userQueryWrapper);
        map.put("email",selectuser.getEmail());
        map.put("id",selectuser.getId());
        return Result.success(map);
    }

    @Override
    public Result logout(String uuid) {
        //将token加入黑名单
        redisCache.setCacheObject("blacklist:"+uuid,"",30,TimeUnit.DAYS);
        return new Result(200,"退出成功",null);
    }
    @Override
    public void addUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public UserInfo selectUserInfoByUserId(Long userId) {
        UserInfo result=new UserInfo();
        User user=userMapper.selectById(userId);
        BeanUtils.copyProperties(user,result);
        return result;
    }

    @Override
    public void updateUserName(Long userId, String userName) {
        User user=new User();
        user.setId(userId);
        user.setUsername(userName);
        userMapper.updateById(user);
    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        User user=new User();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
    }


}