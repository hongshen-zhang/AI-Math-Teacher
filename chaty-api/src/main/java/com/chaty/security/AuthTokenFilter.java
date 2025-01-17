package com.chaty.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    private final String authorizationHeaderName;
    private final TokenAuthService tokenAuthService;
    private final List<RequestMatcher> pathMatchers;

    public AuthTokenFilter(String authorizationHeaderName, TokenAuthService tokenAuthService,
            List<String> protectedUrls) {
        this.authorizationHeaderName = authorizationHeaderName;
        this.tokenAuthService = tokenAuthService;
        this.pathMatchers = protectedUrls.stream()
                .map(pattern -> new AntPathRequestMatcher(pattern))
                .collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 只拦截配置的URL
        if (isProtectedUrl(request)) {
            String authorizationHeader = request.getHeader(authorizationHeaderName);
            try {
                if (authorizationHeader == null) {
                    throw new ServletException("Authorization header is missing");
                }
                TokenAuth<?> authenticate = tokenAuthService.authenticate(authorizationHeader);
                request.setAttribute(TokenAuthConsts.AUTH_KEY, authenticate);
            } catch (Exception e) {
                log.error("Error during authorization check for URI: " + request.getRequestURI(), e);
                // 验证失败处理，例如返回401 Unauthorized
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized");
                return; // 停止请求继续处理
            }
        }

        filterChain.doFilter(request, response); // 继续处理请求
    }

    private boolean isProtectedUrl(HttpServletRequest request) {
        return pathMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
