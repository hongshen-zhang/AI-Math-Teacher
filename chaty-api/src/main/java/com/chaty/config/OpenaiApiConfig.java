package com.chaty.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chaty.api.openai.OpenaiApi;
import com.chaty.exception.BaseException;

import feign.Feign;
import feign.RequestInterceptor;
import feign.Request.Options;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class OpenaiApiConfig {

    @Value("${api.openai.url:https://api.openai.com}")
    public String apiUrl;
    @Value("${api.openai.key}")
    public String apiKey;

    @Bean
    public OpenaiApi openaiApi() {
        return Feign.builder()
                .options(new Options(10, TimeUnit.SECONDS, 180, TimeUnit.SECONDS, true))
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(errorDecoder())
                .requestInterceptor(requestInterceptor())
                .logLevel(feign.Logger.Level.FULL)
                .logger(new Slf4jLogger(OpenaiApi.class))
                .target(OpenaiApi.class, apiUrl);
    }

    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("Authorization", "Bearer " + apiKey);
        };
    }

    ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            return new BaseException(String.format("请求模型失败: %s", response.status()), new BaseException(response.reason()));
        };
    }

}
