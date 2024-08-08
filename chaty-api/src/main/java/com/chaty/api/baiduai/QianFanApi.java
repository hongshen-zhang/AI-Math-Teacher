package com.chaty.api.baiduai;

import java.util.Map;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface QianFanApi {

    @RequestLine("GET /oauth/2.0/token?grant_type=client_credentials&client_id={clientId}&client_secret={clientSecret}")
    Map<String, Object> getAccessToken(@Param("clientId") String clientId, @Param("clientSecret") String clientSecret);

    @RequestLine("POST /rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token={accessToken}")
    @Headers("Content-Type: application/json")
    Map<String, Object> chatWithErnie(@Param("accessToken") String accessToken, Map<String, Object> params);

    @RequestLine("POST /rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant?access_token={accessToken}")
    @Headers("Content-Type: application/json")
    Map<String, Object> chatWithErniePro(@Param("accessToken") String accesToken, Map<String, Object> params);

    @RequestLine("POST /rpc/2.0/ai_custom/v1/wenxinworkshop/embeddings/embedding-v1?access_token={accessToken}")
    @Headers("Content-Type: application/json")
    Map<String, Object> chatWithEbV1(@Param("accessToken") String accesToken, Map<String, Object> params);

    @RequestLine("POST /rpc/2.0/ai_custom/v1/wenxinworkshop/chat/bloomz_7b1?access_token={accessToken}")
    @Headers("Content-Type: application/json")
    Map<String, Object> chatWithBloomz7b1(@Param("accessToken") String accesToken, Map<String, Object> params);

}
