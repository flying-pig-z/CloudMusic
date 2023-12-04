package com.flyingpig.cloudmusic.service;

import com.flyingpig.cloudmusic.dataobject.entity.Collection;
import org.springframework.stereotype.Service;

@Service
public interface CollectionService {

    void collectMusic(Collection collection);

    void deleteCollection(Collection collection);
}
