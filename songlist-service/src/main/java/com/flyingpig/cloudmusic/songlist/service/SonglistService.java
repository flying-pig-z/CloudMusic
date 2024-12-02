package com.flyingpig.cloudmusic.songlist.service;

import com.flyingpig.cloudmusic.songlist.dataobject.dto.SonglistInfo;
import com.flyingpig.cloudmusic.songlist.dataobject.entity.Songlist;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SonglistService {

    Long addSonglist(Songlist songlist);

    List<SonglistInfo> listSonglistByUserId(Long userId);

    void deleteSonglistById(Long id);
}
