package com.flyingpig.cloudmusic.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.music.dataobject.entity.Collection;
import com.flyingpig.cloudmusic.music.dataobject.entity.Like;
import com.flyingpig.cloudmusic.music.dataobject.entity.Music;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicDT0;
import com.flyingpig.cloudmusic.music.mapper.CollectionMapper;
import com.flyingpig.cloudmusic.music.mapper.LikeMapper;
import com.flyingpig.cloudmusic.music.mapper.MusicMapper;
import com.flyingpig.cloudmusic.music.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author flyingpig
 */
@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class MusicServiceImpl implements MusicService {
    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private CollectionMapper collectionMapper;

    @Override
    public MusicDT0 selectMusicVOByUserIdAndMusicId(Long userId, Long musicId) {
        Music music = musicMapper.selectById(musicId);

        MusicDT0 result = new MusicDT0();
        BeanUtils.copyProperties(music, result); // 将 music 对象属性值复制到 result 对象上
        LambdaQueryWrapper<Like> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likeLambdaQueryWrapper.eq(Like::getUserId, userId).eq(Like::getMusicId, musicId);
        Like like = likeMapper.selectOne(likeLambdaQueryWrapper);
        if (like == null) {
            result.setLikeOrNot(false);
        } else {
            result.setLikeOrNot(true);
        }
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper.eq(Collection::getUserId, userId).eq(Collection::getMusicId, musicId);
        Collection collection = collectionMapper.selectOne(collectionLambdaQueryWrapper);
        if (collection == null) {
            result.setCollectOrNot(false);
        }else {
            result.setCollectOrNot(true);
        }
        return result;
    }

}
