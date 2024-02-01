package com.flyingpig.cloudmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyingpig.cloudmusic.dataobject.dto.LoginUser;
import com.flyingpig.cloudmusic.dataobject.entity.User;
import com.flyingpig.cloudmusic.mapper.UserMapper;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.UserService;
import com.flyingpig.cloudmusic.util.MyStringRedisTemplate;
import com.flyingpig.cloudmusic.util.RedisCache;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import cn.hutool.json.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.flyingpig.cloudmusic.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.flyingpig.cloudmusic.util.RedisConstants.USER_INFO_KEY;
import static com.flyingpig.cloudmusic.util.RedisConstants.USER_INFO_TTL;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    RedisCache redisCache;

    @Autowired
    MyStringRedisTemplate myStringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result login(User user) {
        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }
        //如果认证通过了，使用userid生成一个jwt jwt存入ResponseResult返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userid = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userid);
        //把完整的用户信息存入redis  userid作为key
//        redisCache.setCacheObject("login:"+userid,loginUser);
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
    public Result logout(String uuid) {
        //将token加入黑名单
        redisCache.setCacheObject("blacklist:" + uuid, "", 30, TimeUnit.DAYS);
        return new Result(200, "退出成功", null);
    }

    @Override
    public void addUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public UserInfo selectUserInfoByUserId(Long userId) {
        String key = USER_INFO_KEY + userId;
        //1.从redis查询用户信息缓存，并判断缓存是否存在
        String userInfoJson= myStringRedisTemplate.get(key);
        if (userInfoJson != null && !userInfoJson.equals("")) {
            //缓存存在且不为空对象，直接返回
            return JSONUtil.toBean(userInfoJson, UserInfo.class);
        }else if(userInfoJson != null){
            //缓存为空对象，返回空对象给控制层
            return null;
        }
        //2.缓存不存在，根据id查询数据库
        //互斥锁防止缓存击穿
        String lockKey="lock:user-info:"+userId;

        try{
            boolean isLock = myStringRedisTemplate.tryLock(lockKey);
            // 判断互斥锁否获取成功
            if(!isLock){
                //互斥锁失败，则休眠重试
                Thread.sleep(50);
                return selectUserInfoByUserId(userId);
            }
            UserInfo result = new UserInfo();
            User user = userMapper.selectById(userId);
            if (user == null) {
                //数据库不存在，返回空对象，并将空对象写入缓存并返回
                //将空对象写入缓存，防止缓存穿透
                myStringRedisTemplate.set(key,"",USER_INFO_TTL,TimeUnit.DAYS);
                return null;
            } else {
                //数据库存在则写入内存并返回
                BeanUtils.copyProperties(user, result);
                myStringRedisTemplate.set(key,JSONUtil.toJsonStr(result), USER_INFO_TTL, TimeUnit.DAYS);
                return result;
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            //释放互斥锁
            myStringRedisTemplate.unlock(lockKey);
        }

    }

    @Override
    public void updateUserName(Long userId, String userName) {
        User user = new User();
        user.setId(userId);
        user.setUsername(userName);
        userMapper.updateById(user);
        //删除缓存，防止数据不一致
        myStringRedisTemplate.delete(USER_INFO_KEY + userId);

    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        User user = new User();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
        //删除缓存，防止数据不一致
        myStringRedisTemplate.delete(USER_INFO_KEY + userId);
    }


}