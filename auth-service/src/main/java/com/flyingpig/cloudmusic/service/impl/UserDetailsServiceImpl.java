package com.flyingpig.cloudmusic.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyingpig.cloudmusic.dataobject.dto.LoginUser;
import com.flyingpig.cloudmusic.dataobject.entity.User;
import com.flyingpig.cloudmusic.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //查询用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        User user = userMapper.selectOne(queryWrapper);
        //如果没有查询到用户就抛出异常
        if(Objects.isNull(user)){
            throw new RuntimeException("用户名或者密码错误");
        }
        //TODO 根据用户查询权限信息，添加到LoginUser中

        //把数据封装成UserDetails返回
        return new LoginUser(user);
    }
}
