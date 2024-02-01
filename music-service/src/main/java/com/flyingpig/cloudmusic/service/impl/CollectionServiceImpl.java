package com.flyingpig.cloudmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.dataobject.entity.Collection;
import com.flyingpig.cloudmusic.dataobject.entity.Like;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import com.flyingpig.cloudmusic.mapper.CollectionMapper;
import com.flyingpig.cloudmusic.mapper.MusicMapper;
import com.flyingpig.cloudmusic.service.CollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    CollectionMapper collectionMapper;
    @Autowired
    MusicMapper musicMapper;

    @Override
    public void collectMusic(Collection collection) {
        Music music = musicMapper.selectById(collection.getMusicId());
        music.setCollectNum(music.getCollectNum() + 1);
        musicMapper.updateById(music);
        collectionMapper.insert(collection);
    }

    @Override
    public void deleteCollection(Collection collection) {
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper.eq(Collection::getMusicId, collection.getMusicId());
        collectionLambdaQueryWrapper.eq(Collection::getUserId, collection.getUserId());
        collectionMapper.delete(collectionLambdaQueryWrapper);
        Music music = musicMapper.selectById(collection.getMusicId());
        music.setCollectNum(music.getCollectNum() - 1);
        musicMapper.updateById(music);
    }

    @Override
    public boolean isCollectionExist(Collection collection) {
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper.eq(Collection::getMusicId, collection.getMusicId());
        collectionLambdaQueryWrapper.eq(Collection::getUserId, collection.getUserId());
        int count = collectionMapper.selectCount(collectionLambdaQueryWrapper);
        return count > 0;
    }


}
