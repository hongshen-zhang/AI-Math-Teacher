package com.chaty.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.chaty.common.BaseResponse;
import com.chaty.entity.User;
import com.chaty.security.AuthTokenFilter;
import com.chaty.security.BaseUserDetails;
import com.chaty.security.JwtAuthenticationFilter;
import com.chaty.security.JwtTokenService;
import com.chaty.security.TokenAuthConsts;
import com.chaty.security.TokenAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {

    @Resource
    private TokenAuthService orgTokenAuthService;
    @Resource
    private JwtTokenService jwtTokenService;

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        // 允许所有用户访问 /api/*
        return http.authorizeRequests()
                .antMatchers("/api/org/**", "/api/admin/user/login", "/api/metrics/**", "/static/**")
                .permitAll()
                .antMatchers("/api/admin/**")
                .hasAnyAuthority(TokenAuthConsts.ADMIN_TOKEN_ROLE)
                .antMatchers("/api/**")
                .authenticated()
                .and()
                .formLogin(formLogin -> formLogin
                        .loginProcessingUrl("/api/login")
                        .successHandler(successHandler())
                        .failureHandler(failureHandler()))
                .logout(logout -> logout.logoutUrl("/api/logout").logoutSuccessHandler(logoutSuccessHandler()))
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .addFilterBefore(orgAuthTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenService),
                        UsernamePasswordAuthenticationFilter.class)
                .csrf()
                .disable()
                .cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    AuthenticationSuccessHandler successHandler() {
        return new AuthenticationSuccessHandler() {

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                response.setStatus(HttpStatus.OK.value());
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();

                BaseUserDetails principal = (BaseUserDetails) authentication.getPrincipal();
                User user = principal.getUser();
                user.setPassword(null);
                BaseResponse<?> resp = BaseResponse.ok(user);
                out.write(new ObjectMapper().writeValueAsString(resp));
                out.flush();
                out.close();
            }

        };
    }

    AuthenticationFailureHandler failureHandler() {
        return new AuthenticationFailureHandler() {

            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                    AuthenticationException exception) throws IOException, ServletException {
                response.setStatus(HttpStatus.OK.value());
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();

                BaseResponse<?> resp = BaseResponse.error(exception.getMessage());
                out.write(new ObjectMapper().writeValueAsString(resp));
                out.flush();
                out.close();
            }
        };
    }

    AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response,
                    AccessDeniedException accessDeniedException) throws IOException, ServletException {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        };
    }

    LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication)
                    throws IOException, ServletException {
                response.setStatus(HttpStatus.OK.value());
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                BaseResponse<?> resp = BaseResponse.ok("注销成功");
                out.write(new ObjectMapper().writeValueAsString(resp));
                out.flush();
                out.close();
            }
        };
    }

    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedMethod("*");

        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return corsConfigurationSource;
    }

    @Bean
    public AuthTokenFilter orgAuthTokenFilter() {
        return new AuthTokenFilter(TokenAuthConsts.AUTH_HEADER, orgTokenAuthService,
                Arrays.asList("/api/org/**"));
    }

}
