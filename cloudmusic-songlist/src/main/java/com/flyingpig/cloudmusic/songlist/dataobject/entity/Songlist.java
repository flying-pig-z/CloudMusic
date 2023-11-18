package com.flyingpig.cloudmusic.songlist.dataobject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("songlist")
public class Songlist {
    //雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private Long userId;
}
