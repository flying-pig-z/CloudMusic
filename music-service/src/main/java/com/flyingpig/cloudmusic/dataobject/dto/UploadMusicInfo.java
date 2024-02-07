package com.flyingpig.cloudmusic.dataobject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadMusicInfo {
    private Long id;
    private String name;
    private String coverPath;
    private String musicPath;
    private String singerName;
}
