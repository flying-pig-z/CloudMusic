package com.flyingpig.cloudmusic.songlist.service;

import com.flyingpig.cloudmusic.songlist.dataobject.dto.MusicDetailInSonglist;
import com.flyingpig.cloudmusic.songlist.dataobject.entity.SonglistMusic;
import com.flyingpig.feign.dataobject.dto.MusicDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SonglistMusicService {
    List<MusicDetailInSonglist> listMusicNameBySonglistId(Long id);

    void addSonglistMusicList(List<SonglistMusic> songlistMusicList);

    void deleteSonglists(List<Long> songlistIds);
}
