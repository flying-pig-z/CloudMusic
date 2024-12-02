package com.flyingpig.feign.dataobject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCollectInfo {
    private Long musicCollectNum;
    private Boolean collectOrNot;
}
