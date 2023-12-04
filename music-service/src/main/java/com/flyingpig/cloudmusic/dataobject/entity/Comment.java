package com.flyingpig.cloudmusic.dataobject.entity;

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
@TableName("comment")
public class Comment {
    //雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String content;
    private LocalDateTime time;
    private Long userId;
    private Long musicId;
}
