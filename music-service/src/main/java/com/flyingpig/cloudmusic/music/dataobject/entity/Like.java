package com.flyingpig.cloudmusic.music.dataobject.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("like_table")
public class Like {
    //雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long musicId;
}
