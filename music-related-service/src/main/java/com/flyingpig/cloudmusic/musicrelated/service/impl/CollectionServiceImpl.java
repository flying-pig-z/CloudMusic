package com.flyingpig.cloudmusic.musicrelated.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Collection;
import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Music;
import com.flyingpig.cloudmusic.musicrelated.dataobject.message.CollectionMessage;
import com.flyingpig.cloudmusic.musicrelated.mapper.CollectionMapper;
import com.flyingpig.cloudmusic.musicrelated.service.CollectionService;
import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.feign.clients.MusicClients;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    CollectionMapper collectionMapper;
    @Autowired
    MusicClients musicClients;

    @Override
    public void collectMusic(Collection collection) {
        collectionMapper.insert(collection);
        musicClients.incOrDecCollectionNum(collection.getMusicId(), CollectionMessage.INCREASE);
    }

    @Override
    public void deleteCollection(Collection collection) {
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<Collection>()
                .eq(Collection::getMusicId, collection.getMusicId())
                .eq(Collection::getUserId, collection.getUserId());
        collectionMapper.delete(collectionLambdaQueryWrapper);
        musicClients.incOrDecCollectionNum(collection.getMusicId(), CollectionMessage.DECREASE);
    }

    @Override
    public boolean isCollectionExist(Collection collection) {
        return collectionMapper.selectCount(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getMusicId, collection.getMusicId())
                .eq(Collection::getUserId, collection.getUserId())) > 0;
    }

    @Override
    public Long getCollectionNum(Collection collection) {
        return (long) collectionMapper.selectCount(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getMusicId, collection.getMusicId())
                .eq(Collection::getUserId, collection.getUserId()));
    }

    @Override
    public List<MusicDetail> listUserCollection() {
        List<Collection> collections = collectionMapper.selectList(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, UserContext.getUser().getUserId()));
        List<MusicDetail> musicDetails = new ArrayList<>();
        for(Collection collection : collections){
            musicDetails.add(musicClients.selectMusicDetailById(collection.getMusicId()));
        }
        return musicDetails;
    }

    @Override
    public Integer getUserCollectionNum() {
        return collectionMapper.selectCount(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, UserContext.getUser().getUserId()));
    }


}
