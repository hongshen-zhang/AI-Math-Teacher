package com.chaty.common;

import lombok.Data;

@Data
public class BaseResponse<T> {

    public static final int SUCCESS = 200;
    public static final int ERROR = 500;

    private int code;

    private String message;

    private T data;

    private String errlog;

    public static BaseResponse<?> ok(String message) {
        BaseResponse<?> response = new BaseResponse<>();
        response.setCode(SUCCESS);
        response.setMessage(message);
        return response;
    }

    public static <T> BaseResponse<T> ok(T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setCode(SUCCESS);
        response.setData(data);
        return response;
    }

    public static <T> BaseResponse<T> ok(String message, T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setCode(SUCCESS);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static BaseResponse<?> error(String message) {
        BaseResponse<?> response = new BaseResponse<>();
        response.setCode(ERROR);
        response.setMessage(message);
        return response;
    }

    public static BaseResponse<?> error(String message, String errlog) {
        BaseResponse<?> response = new BaseResponse<>();
        response.setCode(ERROR);
        response.setMessage(message);
        response.setErrlog(errlog);
        return response;
    }

}
