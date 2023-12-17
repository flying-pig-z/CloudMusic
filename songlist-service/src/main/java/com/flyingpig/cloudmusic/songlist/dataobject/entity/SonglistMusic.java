package com.flyingpig.cloudmusic.songlist.dataobject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("songlist_music")
public class SonglistMusic {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long songlistId;
    private Long musicId;
}
