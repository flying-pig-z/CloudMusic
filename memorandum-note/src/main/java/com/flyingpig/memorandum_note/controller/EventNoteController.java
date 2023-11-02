package com.flyingpig.memorandum_note.controller;


import com.flyingpig.memorandum_note.common.Result;
import com.flyingpig.memorandum_note.dataobject.entity.EventNote;
import com.flyingpig.memorandum_note.service.EventNoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/event-notes")
public class EventNoteController {
    @Autowired
    private EventNoteService eventNoteService;

    @GetMapping("/notes-of-event")
    public Result listEventNotesByEventId(@RequestParam Integer eventId) {
        //调用service层的添加功能
        List<EventNote> eventNoteList=eventNoteService.listEventNotesByEventId(eventId);
        return Result.success(eventNoteList);
    }

}
