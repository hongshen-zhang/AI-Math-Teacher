package com.chaty.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        // 允许所有用户访问 /api/*
        return http.authorizeRequests()
                .antMatchers("/api/**", "/static/**")
                .anonymous()
                .and()
                .csrf()
                .disable()
                .cors()
                .and()
                .build();
    }

}
