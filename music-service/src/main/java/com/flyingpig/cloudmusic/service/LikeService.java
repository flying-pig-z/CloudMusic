package com.flyingpig.cloudmusic.service;

import com.flyingpig.cloudmusic.dataobject.entity.Like;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {
    void likeMusic(Like like);

    void deleteLike(Like like);

    boolean isLikeExist(Like like);
}
