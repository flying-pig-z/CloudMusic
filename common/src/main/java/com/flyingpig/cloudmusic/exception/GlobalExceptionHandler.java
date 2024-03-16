package com.flyingpig.cloudmusic.exception;


import com.flyingpig.cloudmusic.constant.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.flyingpig.cloudmusic.result.Result;
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
     * 其他异常--500
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        e.printStackTrace();
        return Result.error(StatusCode.SERVERERROR,"对不起，操作失败，请联系管理员");
    }
}

