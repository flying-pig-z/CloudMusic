package com.flyingpig.cloudmusic.musicrelated.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Like;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper extends BaseMapper<Like> {
}
