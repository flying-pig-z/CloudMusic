package com.flyingpig.cloudmusic.songlist.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.songlist.dataobject.dto.SonglistInfo;
import com.flyingpig.cloudmusic.songlist.dataobject.entity.Songlist;
import com.flyingpig.cloudmusic.songlist.dataobject.entity.SonglistMusic;
import com.flyingpig.cloudmusic.songlist.mapper.SonglistMapper;
import com.flyingpig.cloudmusic.songlist.mapper.SonglistMusicMapper;
import com.flyingpig.cloudmusic.songlist.service.SonglistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class SonglistImpl implements SonglistService {
    @Autowired
    private SonglistMapper songlistMapper;

    @Autowired
    private SonglistMusicMapper songlistMusicMapper;


    @Override
    public Long addSonglist(Songlist songlist) {
        songlistMapper.insert(songlist);
        return songlist.getId();
    }

    @Override
    public List<SonglistInfo> listSonglistByUserId(Long userId) {
        List<Songlist> songlistList = songlistMapper.selectList(
                new LambdaQueryWrapper<Songlist>().eq(Songlist::getUserId, userId));
        List<SonglistInfo> songlistInfoList = new ArrayList<>();
        for (Songlist songlist : songlistList) {
            SonglistInfo songlistInfo = new SonglistInfo();
            BeanUtils.copyProperties(songlist, songlistInfo);
            songlistInfo.setMusicCount(songlistMusicMapper.selectCount(
                    new LambdaQueryWrapper<SonglistMusic>().eq(SonglistMusic::getSonglistId, songlist.getId())
            ));
            songlistInfoList.add(songlistInfo);
        }
        return songlistInfoList;
    }

    @Override
    public void deleteSonglistById(Long id) {
        songlistMapper.deleteById(id);
    }

}
