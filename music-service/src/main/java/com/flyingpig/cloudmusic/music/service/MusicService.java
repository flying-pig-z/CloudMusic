package com.flyingpig.cloudmusic.music.service;

import com.flyingpig.cloudmusic.music.dataobject.dto.MusicDT0;
import org.springframework.stereotype.Service;

/**
 * @author flyingpig
 */
@Service
public interface MusicService {

    MusicDT0 selectMusicVOByUserIdAndMusicId(Long userId, Long musicId);
}
