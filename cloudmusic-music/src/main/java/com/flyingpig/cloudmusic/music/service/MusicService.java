package com.flyingpig.cloudmusic.music.service;

import com.flyingpig.cloudmusic.music.dataobject.entity.Music;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author flyingpig
 */
@Service
public interface MusicService {

    List<Music> listEventNotesByEventId(Integer eventId);
}
