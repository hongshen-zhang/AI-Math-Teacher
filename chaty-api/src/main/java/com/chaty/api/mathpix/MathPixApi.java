package com.chaty.api.mathpix;

import java.util.Map;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface MathPixApi {

    @RequestLine("POST /v3/text")
    @Headers({ "Content-Type: application/json", "app_id: {appid}", "app_key: {appkey}" })
    Map<String, Object> text(@Param("appid") String appid, @Param("appkey") String appkey, Map<String, Object> params);

}
