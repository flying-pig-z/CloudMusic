package com.flyingpig.cloudmusic.music.dataobject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicInfo {
    private Long id;
    private String name;
    private String coverPath;
    private String musicPath;
    private String singerName;
    private String uploadUserName;
    private Boolean likeOrNot;
    private Boolean collectOrNot;
}
