package com.flyingpig.cloudmusic.music.dataobject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicIdAndName {
    private Long id;
    private String name;
}
