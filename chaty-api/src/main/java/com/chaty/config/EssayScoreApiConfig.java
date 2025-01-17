package com.chaty.config;

import com.chaty.api.essayScoring.EssayScoreApi;
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
public class EssayScoreApiConfig {
    @Value("${essayScoring.url}")
    public String essayScoringUrl;

    @Bean
    public EssayScoreApi essayScoringApi() {
        return Feign.builder()
                .options(new Request.Options(10, TimeUnit.SECONDS, 180, TimeUnit.SECONDS, true))
                .encoder(new FormEncoder())
                .decoder(new JacksonDecoder())
                .logLevel(Logger.Level.FULL)
                .logger(new Slf4jLogger(EssayScoreApi.class))
                .target(EssayScoreApi.class, essayScoringUrl);
    }

}
