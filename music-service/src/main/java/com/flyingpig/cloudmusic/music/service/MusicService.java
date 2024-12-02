package com.flyingpig.cloudmusic.music.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicDetail;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicInfo;
import com.flyingpig.cloudmusic.music.dataobject.dto.MusicInfoInRankList;
import com.flyingpig.cloudmusic.music.dataobject.dto.UploadMusicInfo;
import com.flyingpig.cloudmusic.music.dataobject.entity.Music;
import org.springframework.stereotype.Service;

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


    Boolean incOrDecLikeNum(Long musicId, String mode);

    Boolean incOrDecCollectionNum(Long musicId, String mode);

    List<MusicInfo> listRandomMusics(Integer num);
}
