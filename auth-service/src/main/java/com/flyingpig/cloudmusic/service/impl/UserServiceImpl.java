package com.flyingpig.cloudmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyingpig.cloudmusic.dataobject.dto.LoginUser;
import com.flyingpig.cloudmusic.dataobject.entity.User;
import com.flyingpig.cloudmusic.mapper.UserMapper;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.service.UserService;
import com.flyingpig.cloudmusic.util.cache.MyStringRedisTemplate;
import com.flyingpig.feign.dataobject.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.flyingpig.cloudmusic.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.flyingpig.cloudmusic.constant.RedisConstants.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;

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
        String uuid = JwtUtil.getUUID();
        String jwt = JwtUtil.createJWT(userid, JwtUtil.JWT_TTL, uuid);

        //将jwt存入redis中，键为userId,值为token的uuid，不直接存jwt可以节省缓存
        myStringRedisTemplate.set(USER_LOGIN_KEY + userid, uuid, USER_LOGIN_TTL, TimeUnit.DAYS);

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
        myStringRedisTemplate.delete(USER_LOGIN_KEY + userId);
        return new Result(200, "退出成功", null);
    }

    @Override
    public void addUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public UserInfo selectUserInfoByUserId(Long userId) {
        return myStringRedisTemplate.safeGetWithLock(USER_INFO_KEY + userId, UserInfo.class, () -> {
            UserInfo result = new UserInfo();
            User user = userMapper.selectById(userId);
            if (user == null) {
                return null;
            }
            BeanUtils.copyProperties(user, result);
            return result;
        }, USER_INFO_TTL, TimeUnit.DAYS);
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

    @Override
    public void deleteUserById(Long userId) {
        userMapper.deleteById(userId);
    }


}