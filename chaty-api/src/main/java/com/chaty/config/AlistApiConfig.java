package com.chaty.config;

import com.chaty.api.alist.AlistApi;
import com.chaty.api.openai.OpenaiApi;
import feign.Feign;
import feign.Logger;
import feign.Request.Options;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Configuration
public class AlistApiConfig {
    
    @Value("${alist.url}")
    public String alistUrl;
    @Value("${alist.token}")
    public String alistToken;

    @Bean
    public AlistApi alistApi() {
        return Feign.builder()
                .options(new Options(10, TimeUnit.SECONDS, 180, TimeUnit.SECONDS, true))
                .encoder(encoder())
                .decoder(new JacksonDecoder())
                // .errorDecoder(errorDecoder())
                .requestInterceptor(requestInterceptor())
                .logLevel(Logger.Level.HEADERS)
                .logger(new Slf4jLogger(OpenaiApi.class))
                .target(AlistApi.class, alistUrl);
    }

    public Encoder encoder() {
        FormEncoder formEncoder = new FormEncoder();
        JacksonEncoder jacksonEncoder = new JacksonEncoder();
        return (object, bodyType, template) -> {
            Collection<String> contentType  = template.headers().get("Content-Type");
            if (Objects.nonNull(contentType) && !contentType.isEmpty()) {
                if (contentType.contains("multipart/form-data")) {
                    formEncoder.encode(object, bodyType, template);
                    return;
                }
            }
            jacksonEncoder.encode(object, bodyType, template);
        };
    }

    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("Authorization", alistToken);
        };
    }

}
