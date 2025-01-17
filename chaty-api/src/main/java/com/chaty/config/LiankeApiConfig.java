package com.chaty.config;

import com.chaty.api.lianke.LiankeApi;
import com.chaty.api.lianke.OpenLiankeApi;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class LiankeApiConfig {

    @Value("${lianke.url}")
    public String liankeUrl;
    @Value("${lianke.openUrl}")
    public String openUrl;

    @Bean
    public LiankeApi liankeApi() {
        return Feign.builder()
                .options(new Request.Options(10, TimeUnit.SECONDS, 180, TimeUnit.SECONDS, true))
                .encoder(new FormEncoder())
                .decoder(new JacksonDecoder())
                // .errorDecoder(errorDecoder())
                .logLevel(Logger.Level.FULL)
                .logger(new Slf4jLogger(LiankeApi.class))
                .target(LiankeApi.class, liankeUrl);
    }

    @Bean
    public OpenLiankeApi OpenLiankeApi() {
        return Feign.builder()
                .options(new Request.Options(10, TimeUnit.SECONDS, 180, TimeUnit.SECONDS, true))
                .encoder(new FormEncoder())
                .decoder(new JacksonDecoder())
                // .errorDecoder(errorDecoder())
                .logLevel(Logger.Level.FULL)
                .logger(new Slf4jLogger(OpenLiankeApi.class))
                .target(OpenLiankeApi.class, openUrl);
    }
}
