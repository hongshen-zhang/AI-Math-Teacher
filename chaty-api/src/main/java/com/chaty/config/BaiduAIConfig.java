package com.chaty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chaty.api.baiduai.QianFanApi;
import com.chaty.exception.BaseException;

import feign.Feign;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;

@Configuration
public class BaiduAIConfig {

    @Value("${api.baiduai.url:https://aip.baidubce.com}")
    private String url;

    @Bean
    public QianFanApi qianFanAPI() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(errorDecoder())
                .logLevel(feign.Logger.Level.FULL)
                .logger(new Slf4jLogger(QianFanApi.class))
                .target(QianFanApi.class, url);
    }

    ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            return new BaseException(String.format("请求模型失败: %s", response.status()),
                    new BaseException(response.reason()));
        };
    }

}
