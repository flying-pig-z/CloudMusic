package com.flyingpig.cloudmusic.musicrelated.service;


import com.flyingpig.cloudmusic.musicrelated.dataobject.entity.Like;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import com.flyingpig.feign.dataobject.dto.UserLikeInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LikeService {
    boolean likeMusic(Like like);

    UserLikeInfo getLikeInfoByUserIdAndMusicId(Long userId, Long musicId);

    List<MusicDetail> listUserLike();

    Integer getUserLikeNum();
}
