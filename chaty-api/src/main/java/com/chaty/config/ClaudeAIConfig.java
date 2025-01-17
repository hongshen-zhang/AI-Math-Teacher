package com.chaty.config;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.chaty.api.claude.ClaudeApi;
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
public class ClaudeAIConfig {

    @Value("${api.claude.url:https://api.anthropic.com}")
    public String apiUrl;
    @Value("${api.claude.key}")
    public String[] apiKey;
    @Value("${api.claude.version:2023-06-01}")
    public String version;

    @Bean
    public ClaudeApi claudeApi() {
        return Feign.builder()
                .options(new Options(10, TimeUnit.SECONDS, 180, TimeUnit.SECONDS, true))
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(errorDecoder())
                .requestInterceptor(requestInterceptor())
                .logLevel(feign.Logger.Level.FULL)
                .logger(new Slf4jLogger(ClaudeApi.class))
                .target(ClaudeApi.class, apiUrl);
    }

    ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            return new BaseException(String.format("请求模型失败: %s", response.status()),
                    new BaseException(response.reason()));
        };
    }

    @Bean
    public WebClient claudeaiWebClient() {
        return WebClient.builder().baseUrl(apiUrl)
                .defaultHeader("anthropic-version", version)
                // .defaultHeader("anthropic-beta", "messages-2023-12-15")
                .defaultHeader("x-api-key", apiKey[0])
                .build();
    }

    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("x-api-key", nextKey());
            template.header("anthropic-version", version);
            template.header("User-Agent", "Application");
        };
    }

    public AtomicInteger keyidx = new AtomicInteger(0);
    public String nextKey() {
        return apiKey[keyidx.getAndUpdate(k -> (k + 1) % apiKey.length)]; 
    }
    
}
