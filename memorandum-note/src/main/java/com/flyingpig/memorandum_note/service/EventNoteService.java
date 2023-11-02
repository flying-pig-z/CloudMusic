package com.flyingpig.memorandum_note.service;

import com.flyingpig.memorandum_note.dataobject.entity.EventNote;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author flyingpig
 */
@Service
public interface EventNoteService {

    List<EventNote> listEventNotesByEventId(Integer eventId);
}
