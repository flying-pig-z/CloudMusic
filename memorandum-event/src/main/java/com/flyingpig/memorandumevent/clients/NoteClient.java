package com.flyingpig.memorandumevent.clients;

import com.flyingpig.memorandumevent.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("noteservice")
public interface NoteClient {
    @GetMapping("/event-notes/notes-of-event")
    public Result listEventNotesByEventId(@RequestParam Integer eventId) ;
}
