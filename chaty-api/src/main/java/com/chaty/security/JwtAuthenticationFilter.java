package com.chaty.security;

import java.io.IOException;
import java.util.Objects;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.chaty.dto.AdminUserDTO;

import cn.hutool.core.collection.ListUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 从请求头中获取令牌
        String token = extractToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 验证令牌并获取用户名
                AdminUserDTO adminUser = jwtTokenService.validateTokenAndGetAdminUser(token);
                if (Objects.nonNull(adminUser)) {
                    // 创建认证对象
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(TokenAuthConsts.ADMIN_TOKEN_ROLE);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            adminUser, token, ListUtil.of(authority));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 设置认证信息到安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (AuthenticationException e) {
                onAuthError(response, e.getMessage());
                return;
            } catch (Exception e) {
                log.error("Token verification failed", e);
                onAuthError(response, "Token verification failed");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader(TokenAuthConsts.ADMIN_TOKEN_HEADER);
        if (StringUtils.hasText(token)) {
            return token;
        }
        return null;
    }

    private void onAuthError(HttpServletResponse response, String message) throws ServletException, IOException {
        log.error("Auth error: {}", message);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(message);
    }
}
