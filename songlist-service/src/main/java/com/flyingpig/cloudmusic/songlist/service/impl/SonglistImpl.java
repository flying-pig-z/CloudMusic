package com.flyingpig.cloudmusic.songlist.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.songlist.dataobject.dto.SonglistInfo;
import com.flyingpig.cloudmusic.songlist.dataobject.entity.Songlist;
import com.flyingpig.cloudmusic.songlist.mapper.SonglistMapper;
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


    @Override
    public void addSonglist(Songlist songlist) {
        songlistMapper.insert(songlist);
    }

    @Override
    public List<SonglistInfo> listSonglistByUserId(Long userId) {
        LambdaQueryWrapper<Songlist> songlistLambdaQueryWrapper=new LambdaQueryWrapper<>();
        songlistLambdaQueryWrapper.eq(Songlist::getUserId,userId);
        List<Songlist> songlistList=songlistMapper.selectList(songlistLambdaQueryWrapper);
        List<SonglistInfo> songlistInfoList=new ArrayList<>();
        for(Songlist songlist:songlistList){
            SonglistInfo songlistInfo=new SonglistInfo();
            BeanUtils.copyProperties(songlist,songlistInfo);
            songlistInfoList.add(songlistInfo);
        }
        return songlistInfoList;
    }

    @Override
    public void deleteSonglistById(Long id) {
        songlistMapper.deleteById(id);
    }

}
