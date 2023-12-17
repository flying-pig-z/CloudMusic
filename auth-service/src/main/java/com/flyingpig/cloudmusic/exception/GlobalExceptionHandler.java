package com.flyingpig.cloudmusic.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.flyingpig.cloudmusic.result.Result;

//全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result ex(Exception ex) {
        ex.printStackTrace();
        if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
            return Result.error(401,"token过期或没有权限");
        }
        return Result.error(500,"对不起，操作失败，请联系管理员");
    }
}

