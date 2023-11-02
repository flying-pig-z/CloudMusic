package com.flyingpig.memorandumevent.dataobject.enetity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flyingpig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("event_note")
public class EventNote {
    //雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String content;
    private Long eventId;
}
