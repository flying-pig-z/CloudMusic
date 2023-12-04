package com.flyingpig.cloudmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.dataobject.entity.Collection;
import com.flyingpig.cloudmusic.mapper.CollectionMapper;
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

    @Override
    public void collectMusic(Collection collection) {
        collectionMapper.insert(collection);
    }

    @Override
    public void deleteCollection(Collection collection) {
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper=new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper.eq(Collection::getMusicId,collection.getMusicId());
        collectionLambdaQueryWrapper.eq(Collection::getUserId,collection.getUserId());
        collectionMapper.delete(collectionLambdaQueryWrapper);
    }


}
