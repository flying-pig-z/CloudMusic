package com.flyingpig.cloudmusic.musicrelated.dataobject.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeMessage implements Serializable {
    private String musicId;
    private String userId;
    private String mode;

    public static final String INCREASE = "increase";
    public static final String DECREASE = "decrease";

}
