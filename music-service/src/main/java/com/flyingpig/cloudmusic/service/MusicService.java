package com.flyingpig.cloudmusic.service;

import com.flyingpig.cloudmusic.dataobject.dto.*;
import com.flyingpig.cloudmusic.dataobject.entity.Music;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author flyingpig
 */
@Service
public interface MusicService {

    MusicInfo selectMusicInfoByUserIdAndMusicId(Long userId, Long musicId);

    List<MusicInfoInRankList> selectRankList();

    MusicDetail selectMusicDetailByMusicId(Long musicId);

    List<MusicIdAndName> searchMusic(String keyword);

    void addMusic(Music music);

    void deleteMusicByIdAndUserId(Long musicId, Long userId);

    List<UploadMusicInfo> selectUploadMusics();
}
