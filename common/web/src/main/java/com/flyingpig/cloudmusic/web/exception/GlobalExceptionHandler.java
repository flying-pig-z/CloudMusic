package com.flyingpig.cloudmusic.web.exception;


import com.flyingpig.cloudmusic.web.Result;
import com.flyingpig.cloudmusic.web.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.BindException;

//全局异常处理器
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 请求参数异常/缺少--400
     *
     * @param e
     * @return
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            BindException.class}
    )
    public Result missingServletRequestParameterException(Exception e) {
        return Result.error(StatusCode.PARAMETERERROR, "缺少参数或参数错误");
    }

    /**
     * 请求方法错误--405
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodNotSupportedExceptionHandler(Exception e){
        log.error("请求方法错误");
        return Result.error(StatusCode.METHODERROR,"请求方法错误");
    }

    /**
     * Redis没连接上--500
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    public Result edisConnectionFailureExceptionHandler(RedisConnectionFailureException e) {
        log.error("redis连接错误啦啦啦啦啦啦");
        return Result.error(StatusCode.SERVERERROR,"redis连接错误啦啦啦啦啦啦");
    }


    /**
     * 其他异常--500
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        e.printStackTrace();
        return Result.error(StatusCode.SERVERERROR,e.toString());
    }
}

