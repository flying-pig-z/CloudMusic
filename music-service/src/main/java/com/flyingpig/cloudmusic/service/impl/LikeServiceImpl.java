package com.flyingpig.cloudmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.flyingpig.cloudmusic.dataobject.entity.Like;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import com.flyingpig.cloudmusic.mapper.LikeMapper;
import com.flyingpig.cloudmusic.mapper.MusicMapper;
import com.flyingpig.cloudmusic.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class LikeServiceImpl implements LikeService {
    @Autowired
    LikeMapper likeMapper;
    @Autowired
    MusicMapper musicMapper;

    @Override
    public void likeMusic(Like addLike) {
        Music music=musicMapper.selectById(addLike.getMusicId());
        music.setLikeNum(music.getLikeNum()+1);
        musicMapper.updateById(music);
        likeMapper.insert(addLike);
    }

    @Override
    public void deleteLike(Like like) {
        Music music=musicMapper.selectById(like.getMusicId());
        music.setLikeNum(music.getLikeNum()-1);
        musicMapper.updateById(music);
        LambdaQueryWrapper<Like> likeLambdaQueryWrapper=new LambdaQueryWrapper<>();
        likeLambdaQueryWrapper.eq(Like::getMusicId,like.getMusicId());
        likeLambdaQueryWrapper.eq(Like::getUserId,like.getUserId());
        likeMapper.delete(likeLambdaQueryWrapper);
    }
}
