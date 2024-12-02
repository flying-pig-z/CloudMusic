package com.flyingpig.cloudmusic.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicInfo;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicInfoInRankList;
import com.flyingpig.cloudmusic.music.dataobject.entity.Music;
import com.flyingpig.cloudmusic.music.dataobject.es.MusicDoc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MusicMapper extends BaseMapper<Music> {

    List<MusicInfoInRankList> selectRankList();


    List<MusicDoc> selectAll();

    @Select("SELECT id FROM music")
    List<Long> getAllMusicIds();



}
