package com.flyingpig.cloudmusic.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyingpig.cloudmusic.music.dataobject.entity.Music;
import com.flyingpig.cloudmusic.music.mapper.MusicMapper;
import com.flyingpig.cloudmusic.music.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author flyingpig
 */
@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class MusicServiceImpl implements MusicService {
    @Autowired
    private MusicMapper eventNoteMapper;

    @Override
    public List<Music> listEventNotesByEventId(Integer eventId) {
        QueryWrapper<Music> eventNoteQueryWrapper=new QueryWrapper<>();
        eventNoteQueryWrapper.eq("event_id",eventId);
        List<Music> eventNoteList=eventNoteMapper.selectList(eventNoteQueryWrapper);
        return eventNoteList;
    }
}
