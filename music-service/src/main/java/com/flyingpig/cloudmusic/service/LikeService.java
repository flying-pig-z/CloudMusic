package com.flyingpig.cloudmusic.service;

import com.flyingpig.cloudmusic.dataobject.entity.Like;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {
    boolean likeMusic(Like like);
}
