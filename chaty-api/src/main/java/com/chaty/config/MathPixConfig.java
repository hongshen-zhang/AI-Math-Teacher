package com.chaty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chaty.api.mathpix.MathPixApi;
import com.chaty.exception.BaseException;

import feign.Feign;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;

@Configuration
public class MathPixConfig {

    @Value("${api.mathpix.url:https://api.mathpix.com}")
    private String url;

    @Bean
    public MathPixApi mathPixApi() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(errorDecoder())
                .logLevel(feign.Logger.Level.FULL)
                .logger(new Slf4jLogger(MathPixApi.class))
                .target(MathPixApi.class, url);
    }

    ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            return new BaseException(String.format("请求图片识别失败: %s", response.status()),
                    new BaseException(response.reason()));
        };
    }

}
