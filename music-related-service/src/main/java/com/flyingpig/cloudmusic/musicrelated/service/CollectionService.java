package com.flyingpig.cloudmusic.musicrelated.service;

import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Collection;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CollectionService {

    void collectMusic(Collection collection);

    void deleteCollection(Collection collection);

    boolean isCollectionExist(Collection collection);

    Long getCollectionNum(Collection collection);

    List<MusicDetail> listUserCollection();

    Integer getUserCollectionNum();
}
