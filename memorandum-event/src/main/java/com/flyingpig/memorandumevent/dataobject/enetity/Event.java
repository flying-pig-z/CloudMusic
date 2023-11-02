package com.flyingpig.memorandumevent.dataobject.enetity;

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
@TableName("event")
public class Event {
    //雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String content;
    private LocalDateTime deadline;
    private Long status;
    private Long userId;
}
