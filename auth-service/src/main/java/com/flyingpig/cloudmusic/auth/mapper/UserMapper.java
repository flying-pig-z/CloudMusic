package com.flyingpig.cloudmusic.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flyingpig.cloudmusic.auth.dataobject.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
