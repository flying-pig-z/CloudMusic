package com.flyingpig.cloudmusic.musicrelated.dataobject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicInfoInRankList implements Serializable {
    private Long id;
    private String name;
    private String coverPath;
    private String musicPath;
    private String singerName;
}
