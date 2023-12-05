package com.flyingpig.cloudmusic.songlist.dataobject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SonglistInfo {
    //雪花算法生成
    private Long id;
    private String name;
}
