package com.flyingpig.cloudmusic.gateway.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;//响应码，token过期返回0，正确返回1，异常返回2
    private String msg;//响应信息，描述字符串
    private Object data;//返回的数据

    //增删改
    public static Result success() {
        return new Result(StatusCode.OK, "success", null);
    }

    //查询 成功响应
    public static Result success(Object data) {
        return new Result(StatusCode.OK, "success", data);
    }

    //失败响应
    public static Result error(Integer code,String msg) {
        return new Result(code, msg, null);
    }

    public static Result error(String msg) {
        return new Result(StatusCode.SERVERERROR, msg, null);
    }

}