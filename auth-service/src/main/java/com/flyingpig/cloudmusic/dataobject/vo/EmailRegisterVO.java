package com.flyingpig.cloudmusic.dataobject.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegisterVO {
    public String email;
    public String verificationCode;
    //用户名
    private String username;
    //密码
    public String password;

}
