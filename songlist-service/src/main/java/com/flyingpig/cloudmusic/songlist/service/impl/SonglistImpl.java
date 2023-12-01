package com.flyingpig.cloudmusic.songlist.service.impl;

import com.flyingpig.cloudmusic.songlist.mapper.SonglistMapper;
import com.flyingpig.cloudmusic.songlist.service.SonglistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class SonglistImpl implements SonglistService {
    @Autowired
    private SonglistMapper songlistMapper;




}
