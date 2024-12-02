package com.flyingpig.feign.dataobject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLikeInfo {
    private Long musicLikeNum;
    private Boolean likeOrNot;
}
