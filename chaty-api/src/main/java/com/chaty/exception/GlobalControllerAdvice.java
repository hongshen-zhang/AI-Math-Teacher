package com.chaty.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RestController
public class GlobalControllerAdvice {

    @Value("${config.logerrdetail:false}")
    public boolean logerrdetail;

    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handleException(Exception e) {
        log.error("BaseException exception handler catched exception", e);
        if (logerrdetail) {
            return BaseResponse.error(e.getMessage(), ExceptionUtil.stacktraceToString(e));
        } else {
            return BaseResponse.error(e.getMessage());
        }
    }

}
