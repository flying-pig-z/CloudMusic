package com.flyingpig.memorandum_note.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyingpig.memorandum_note.dataobject.entity.EventNote;
import com.flyingpig.memorandum_note.mapper.EventNoteMapper;
import com.flyingpig.memorandum_note.service.EventNoteService;
import jdk.jfr.Event;
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
public class EventNoteServiceImpl implements EventNoteService {
    @Autowired
    private EventNoteMapper eventNoteMapper;

    @Override
    public List<EventNote> listEventNotesByEventId(Integer eventId) {
        QueryWrapper<EventNote> eventNoteQueryWrapper=new QueryWrapper<>();
        eventNoteQueryWrapper.eq("event_id",eventId);
        List<EventNote> eventNoteList=eventNoteMapper.selectList(eventNoteQueryWrapper);
        return eventNoteList;
    }
}
