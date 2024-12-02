package com.flyingpig.cloudmusic.musicrelated.dataobject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("collection")
public class Collection {
    //雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long musicId;
}
