package com.flyingpig.cloudmusic.songlist.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyingpig.cloudmusic.songlist.dataobject.dto.MusicDetailInSonglist;
import com.flyingpig.cloudmusic.songlist.dataobject.entity.SonglistMusic;
import com.flyingpig.cloudmusic.songlist.mapper.SonglistMusicMapper;
import com.flyingpig.cloudmusic.songlist.service.SonglistMusicService;
import com.flyingpig.feign.clients.MusicClients;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class SonglistMusicServiceImpl extends ServiceImpl<SonglistMusicMapper,SonglistMusic> implements SonglistMusicService {
    @Autowired
    SonglistMusicMapper songlistMusicMapper;
    @Autowired
    MusicClients musicClients;
    @Override
    public List<MusicDetailInSonglist> listMusicNameBySonglistId(Long id) {
        LambdaQueryWrapper<SonglistMusic> songlistMusicLambdaQueryWrapper=new LambdaQueryWrapper<>();
        songlistMusicLambdaQueryWrapper.eq(SonglistMusic::getSonglistId,id);
        List<SonglistMusic> songlistMusics=songlistMusicMapper.selectList(songlistMusicLambdaQueryWrapper);
        List<MusicDetailInSonglist> result=new ArrayList<>();
        for(SonglistMusic songlistMusic:songlistMusics){
            MusicDetail musicDetail=musicClients.selectMusicDetailById(songlistMusic.getMusicId());
            MusicDetailInSonglist musicDetailInSonglist=new MusicDetailInSonglist(songlistMusic.getId(),musicDetail);
            result.add(musicDetailInSonglist);
        }
        return result;
    }

    @Override
    public void addSonglistMusicList(List<SonglistMusic> songlistMusicList) {
        for(SonglistMusic songlistMusic:songlistMusicList){
            songlistMusicMapper.insert(songlistMusic);
        }
    }

    @Override
    public void deleteSonglists(List<Long> songlistIds) {
        songlistMusicMapper.deleteBatchIds(songlistIds);
    }
}
