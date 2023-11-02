package com.flyingpig.memorandumevent.controller;


import com.flyingpig.memorandumevent.common.Result;
import com.flyingpig.memorandumevent.dataobject.dto.EventWithNotes;
import com.flyingpig.memorandumevent.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventService eventService;
    @GetMapping("/{id}")
    public Result selectEventDetailByEventId(@PathVariable("id") Integer eventId) {
        //调用service层的添加功能
        EventWithNotes eventWithNotes=eventService.selectEventDetailByEventId(eventId);
        return Result.success(eventWithNotes);
    }


}
