package com.flyingpig.cloudmusic.songlist.dataobject.dto;

import com.flyingpig.feign.dataobject.dto.MusicDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicDetailInSonglist {
    @ApiModelProperty("歌单的id")
    Long songlistId;
    @ApiModelProperty("歌曲的详细信息")
    MusicDetail musicDetail;
}
