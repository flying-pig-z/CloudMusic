package com.flyingpig.memorandumevent.service;

import com.flyingpig.memorandumevent.dataobject.dto.EventWithNotes;
import org.springframework.stereotype.Service;

@Service
public interface EventService {

    EventWithNotes selectEventDetailByEventId(Integer eventId);
}
