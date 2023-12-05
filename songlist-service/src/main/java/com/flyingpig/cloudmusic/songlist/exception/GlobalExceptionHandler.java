package com.flyingpig.cloudmusic.songlist.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import result.Result;

//全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result ex(Exception ex) {
        ex.printStackTrace();
        if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
            return Result.tokenExpire("token过期");
        }
        return Result.error("对不起，操作失败，请联系管理员");
    }
}
