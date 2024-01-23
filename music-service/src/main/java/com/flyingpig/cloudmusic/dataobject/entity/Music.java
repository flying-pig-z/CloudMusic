package com.flyingpig.cloudmusic.dataobject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author flyingpig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("music")
public class Music implements Serializable {
    //雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private String introduce;
    private String coverPath;
    private String musicPath;
    private Long likeNum;
    private Long collectNum;
    private Long uploadUser;
    private String singerName;
}
