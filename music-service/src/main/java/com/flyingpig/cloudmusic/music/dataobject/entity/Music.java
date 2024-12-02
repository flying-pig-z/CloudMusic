package com.flyingpig.cloudmusic.music.dataobject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author flyingpig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("music")
@Accessors(chain = true)
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
    private LocalDateTime uploadTime;
}
