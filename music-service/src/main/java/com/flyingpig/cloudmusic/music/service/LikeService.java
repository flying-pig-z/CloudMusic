package com.flyingpig.cloudmusic.music.service;

import com.flyingpig.cloudmusic.music.dataobject.entity.Like;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {
    void likeMusic(Like like);

    void deleteLike(Like like);
}
