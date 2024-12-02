package com.flyingpig.cloudmusic.musicrelated.dataobject.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AddComment {
    //雪花算法生成
    private String content;
    private Long musicId;
}
