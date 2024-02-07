package com.flyingpig.cloudmusic.dataobject.message;

import com.flyingpig.cloudmusic.dataobject.entity.Music;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeMessage implements Serializable {
    private Long musicId;
    private Long userId;
    private Long likeNum;

}
