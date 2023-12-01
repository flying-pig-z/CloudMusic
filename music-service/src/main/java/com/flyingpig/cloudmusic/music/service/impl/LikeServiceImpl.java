package com.flyingpig.cloudmusic.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.music.dataobject.entity.Like;
import com.flyingpig.cloudmusic.music.mapper.LikeMapper;
import com.flyingpig.cloudmusic.music.service.LikeService;
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

    @Override
    public void likeMusic(Like addLike) {
        likeMapper.insert(addLike);
    }

    @Override
    public void deleteLike(Like like) {
        LambdaQueryWrapper<Like> likeLambdaQueryWrapper=new LambdaQueryWrapper<>();
        likeLambdaQueryWrapper.eq(Like::getMusicId,like.getMusicId());
        likeLambdaQueryWrapper.eq(Like::getUserId,like.getUserId());
        likeMapper.delete(likeLambdaQueryWrapper);
    }
}
