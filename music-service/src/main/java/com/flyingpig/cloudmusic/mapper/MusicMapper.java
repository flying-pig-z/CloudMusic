package com.flyingpig.cloudmusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flyingpig.cloudmusic.dataobject.dto.MusicInfoInRankList;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface MusicMapper extends BaseMapper<Music> {

    List<MusicInfoInRankList> selectRankList();
}
