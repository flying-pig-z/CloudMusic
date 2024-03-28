package com.flyingpig.cloudmusic.interceptor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalUserInfo {
    Long userId;
    String role;
}
