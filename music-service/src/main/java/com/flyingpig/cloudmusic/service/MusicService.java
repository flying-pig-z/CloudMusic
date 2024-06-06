package com.flyingpig.cloudmusic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.cloudmusic.dataobject.dto.*;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import com.flyingpig.cloudmusic.dataobject.es.MusicDoc;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author flyingpig
 */
@Service
public interface MusicService extends IService<Music> {

    MusicInfo selectMusicInfoByUserIdAndMusicId(Long userId, Long musicId);

    List<MusicInfoInRankList> selectRankList();

    MusicDetail selectMusicDetailByMusicId(Long musicId);

    void deleteMusicByIdAndUserId(Long musicId, Long userId);

    List<UploadMusicInfo> selectUploadMusics();

    void deleteMusicById(Long musicId);


}
