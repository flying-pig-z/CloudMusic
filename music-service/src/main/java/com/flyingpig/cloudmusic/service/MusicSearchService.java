package com.flyingpig.cloudmusic.service;

import com.flyingpig.cloudmusic.dataobject.dto.MusicIdAndName;
import com.flyingpig.cloudmusic.dataobject.es.MusicDoc;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface MusicSearchService {
    void musicEsInit() throws IOException;

    List<MusicDoc> searchMusicByEs(String keyword) throws IOException;

    List<MusicIdAndName> searchMusic(String keyword);
}
