package com.flyingpig.memorandumevent.dataobject.dto;

import com.flyingpig.memorandumevent.dataobject.enetity.EventNote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventWithNotes {
    String content;
    LocalDateTime deadline;
    Long status;
    List<EventNote> eventNoteList;
}
