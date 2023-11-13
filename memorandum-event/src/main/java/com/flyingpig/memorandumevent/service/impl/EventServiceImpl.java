package com.flyingpig.memorandumevent.service.impl;

import com.flyingpig.memorandumevent.clients.NoteClient;
import com.flyingpig.memorandumevent.common.Result;
import com.flyingpig.memorandumevent.dataobject.dto.EventWithNotes;
import com.flyingpig.memorandumevent.dataobject.enetity.Event;
import com.flyingpig.memorandumevent.dataobject.enetity.EventNote;
import com.flyingpig.memorandumevent.mapper.EventMapper;
import com.flyingpig.memorandumevent.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class EventServiceImpl implements EventService {
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private NoteClient noteClient;

    @Override
    public EventWithNotes selectEventDetailByEventId(Integer eventId) {
        Event event=eventMapper.selectById(eventId);
        //发起调用
        Result eventNoteListResult = noteClient.listEventNotesByEventId(eventId);
        //存入eventNoteList
        EventWithNotes eventWithNotes=new EventWithNotes();
        eventWithNotes.setContent(event.getContent());
        eventWithNotes.setDeadline(event.getDeadline());
        eventWithNotes.setStatus(event.getStatus());
        eventWithNotes.setEventNoteList((List<EventNote>) eventNoteListResult.getData());
        return eventWithNotes;
    }
}
