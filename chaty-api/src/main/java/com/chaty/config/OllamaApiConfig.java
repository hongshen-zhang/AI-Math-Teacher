package com.chaty.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chaty.api.ollama.OllamaApi;
import com.chaty.exception.BaseException;

import feign.Feign;
import feign.Request.Options;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class OllamaApiConfig {

    @Value("${api.ollama.url}")
    private String apiUrl;

    @Bean
    public OllamaApi ollamaApi() {
        return Feign.builder()
                .options(new Options(10, TimeUnit.SECONDS, 180, TimeUnit.SECONDS, true))
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(errorDecoder())
                .logLevel(feign.Logger.Level.FULL)
                .logger(new Slf4jLogger(OllamaApi.class))
                .target(OllamaApi.class, apiUrl);
    }

    ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            log.error("请求Ollama模型失败: {} \n{}", response.status(), response.reason());
            return new BaseException(String.format("请求模型失败: %s \n%s", response.status(),
                    response.reason()));
        };
    }

}
