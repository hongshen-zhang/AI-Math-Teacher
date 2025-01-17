package com.chaty.config;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.chaty.api.openai.OpenaiApi;
import com.chaty.api.openai.OpenaiAuth;
import com.chaty.api.openai.OpenaiAuthManager;
import com.chaty.api.openai.OpenaiUtil;
import com.chaty.exception.BaseException;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import feign.Feign;
import feign.Request.Options;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class OpenaiApiConfig {

    @Value("${api.openai.url:https://api.openai.com/v1}")
    public String apiUrl;
    @Value("${api.openai.webclient.key}")
    public String webclientKey;

    @Resource
    private OpenaiAuthManager openaiAuthManager;

    @Bean
    public OpenaiApi openaiApi() {
        return Feign.builder()
                .options(new Options(10, TimeUnit.SECONDS, 180, TimeUnit.SECONDS, true))
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(errorDecoder())
                .requestInterceptor(requestInterceptor())
                .logLevel(feign.Logger.Level.HEADERS)
                .logger(new Slf4jLogger(OpenaiApi.class))
                .target(OpenaiApi.class, apiUrl);
    }

    public RequestInterceptor requestInterceptor() {
        return template -> {
            JSONObject bodyObj =  OpenaiUtil.getRequestObj(template);
            OpenaiAuth auth = openaiAuthManager.nextApiKey(bodyObj.getStr("model"));
            template.target(auth.getUrl());
            template.header("Authorization", "Bearer " + auth.getKey());
            bodyObj.set("model", auth.getModel());
            OpenaiUtil.setRequestBody(template, bodyObj);
        };
    }

    ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            String apiKey = OpenaiUtil.getApiKey(response.request());
            // RequestTemplate requestTemplate = response.request().requestTemplate();
            // JSONObject bodyObj = OpenaiUtil.getRequestObj(requestTemplate);
            // String model = bodyObj.getStr("model");
            // if (response.status() == 429) {
            //     openaiAuthManager.onRateLimited(apiKey, model);
            // }
            // if (response.status() == 401) {
            //     openaiAuthManager.onUnauthorized(apiKey, model);
            // }
            String responseBody = "";
            if (Objects.nonNull(response.body())) {
                try {
                    responseBody = IoUtil.read(response.body().asInputStream(), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    log.error("请求模型失败, read response body error", e);
                }
            }
            return new BaseException(String.format("请求模型失败: %s %s %s",
                    response.status(), apiKey, responseBody));
        };
    }

    @Bean
    public WebClient openaiWebClient() {
        return WebClient.builder().baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + webclientKey)
                .build();
    }

}
