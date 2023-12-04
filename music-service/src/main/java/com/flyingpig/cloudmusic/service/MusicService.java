package com.flyingpig.cloudmusic.service;

import com.flyingpig.cloudmusic.dataobject.dto.MusicDT0;
import com.flyingpig.cloudmusic.dataobject.dto.MusicInfoInRankList;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author flyingpig
 */
@Service
public interface MusicService {

    MusicDT0 selectMusicVOByUserIdAndMusicId(Long userId, Long musicId);

    List<MusicInfoInRankList> selectRankList();
}
