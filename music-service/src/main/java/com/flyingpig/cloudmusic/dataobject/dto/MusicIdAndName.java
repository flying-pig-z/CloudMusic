package com.flyingpig.cloudmusic.dataobject.dto;

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
