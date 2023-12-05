package com.flyingpig.cloudmusic.songlist.service.impl;

import com.flyingpig.cloudmusic.songlist.service.SonglistMusicService;
import com.flyingpig.cloudmusic.songlist.service.SonglistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class SonglistMusicServiceImpl implements SonglistMusicService {
}
