package com.chaty.api.openai;

import java.util.Map;

import feign.Headers;
import feign.RequestLine;

public interface OpenaiApi {

    @RequestLine("POST /v1/chat/completions")
    @Headers("Content-Type: application/json")
    Map<String, Object> chatCompletionsV1(Map<String, Object> param);

}
