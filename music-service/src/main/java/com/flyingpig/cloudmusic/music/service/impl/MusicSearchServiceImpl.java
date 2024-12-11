package com.flyingpig.cloudmusic.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicSearchResult;
import com.flyingpig.cloudmusic.music.dataobject.entity.Music;
import com.flyingpig.cloudmusic.music.dataobject.es.MusicDoc;
import com.flyingpig.cloudmusic.music.mapper.MusicMapper;
import com.flyingpig.cloudmusic.music.service.MusicSearchService;
import com.flyingpig.cloudmusic.music.util.ElasticSearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author flyingpig
 */
@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class MusicSearchServiceImpl implements MusicSearchService {

    @Autowired
    MusicMapper musicMapper;

    @Autowired
    ElasticSearchUtil elasticSearchUtil;

    @Override
    public void musicEsInit() throws IOException {
        elasticSearchUtil.deleteIndex("music");
        elasticSearchUtil.createIndex("music");
        List<MusicDoc> musicDocList = musicMapper.selectAll();
        for (MusicDoc musicDoc : musicDocList) {
            ObjectMapper objectMapper = new ObjectMapper();
            elasticSearchUtil.importDocument("music", String.valueOf(musicDoc.getId()), objectMapper.writeValueAsString(musicDoc));
        }
    }

    @Override
    public List<MusicDoc> searchMusicByEs(String keyword) throws IOException {
        return elasticSearchUtil.searchMusicByKeyword(keyword);
    }

    @Override
    public List<MusicSearchResult> searchMusic(String keyword) {
        LambdaQueryWrapper<Music> musicLambdaQueryWrapper = new LambdaQueryWrapper<>();
        musicLambdaQueryWrapper.like(Music::getName, keyword);
        List<Music> musics = musicMapper.selectList(musicLambdaQueryWrapper);
        List<MusicSearchResult> result = new ArrayList<>();
        for (Music music : musics) {
            MusicSearchResult musicIdAndName = new MusicSearchResult();
            BeanUtils.copyProperties(music, musicIdAndName);
            result.add(musicIdAndName);
        }
        return result;
    }
}
