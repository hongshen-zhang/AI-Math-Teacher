package com.chaty.config;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.chaty.api.qianwen.QianwenApi;
import com.chaty.exception.BaseException;

import feign.Feign;
import feign.Request.Options;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class QianwenAIConfig {

    @Value("${api.qianwen.url:https://dashscope.aliyuncs.com}")
    public String apiUrl;
    @Value("${api.qianwen.key}")
    public String[] apiKey;

    @Bean
    public QianwenApi qianwenApi() {
        return Feign.builder()
                .options(new Options(10, TimeUnit.SECONDS, 180, TimeUnit.SECONDS, true))
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(errorDecoder())
                .requestInterceptor(requestInterceptor())
                .logLevel(feign.Logger.Level.FULL)
                .logger(new Slf4jLogger(QianwenApi.class))
                .target(QianwenApi.class, apiUrl);
    }

    ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            return new BaseException(String.format("请求模型失败: %s", response.status()),
                    new BaseException(response.reason()));
        };
    }

    @Bean
    public WebClient qianwenWebClient() {
        return WebClient.builder().baseUrl(apiUrl)
                .defaultHeader("Authorization", apiKey[0])
                .build();
    }

    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("Authorization", nextKey());
        };
    }

    public AtomicInteger keyidx = new AtomicInteger(0);
    public String nextKey() {
        return apiKey[keyidx.getAndUpdate(k -> (k + 1) % apiKey.length)]; 
    }
    
}
