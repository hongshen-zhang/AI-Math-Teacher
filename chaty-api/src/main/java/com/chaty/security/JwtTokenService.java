package com.chaty.security;

import java.util.HashMap;
import java.util.Map;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chaty.dto.AdminUserDTO;
import com.chaty.entity.admin.AdminUser;

import cn.hutool.core.convert.Convert;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

@Service
public class JwtTokenService {
    @Value("${jwt.secret:admin123.}")
    private String secret;

    @Value("${jwt.expiration:3600}")
    private Long expiration;

    public String generateAdminToken(AdminUser user) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("timestamp", System.currentTimeMillis());
        headers.put("expireAt", System.currentTimeMillis() + (expiration * 1000));

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("nickname", user.getNickname());

        return JWTUtil.createToken(headers, claims, secret.getBytes());
    }

    public AdminUserDTO validateTokenAndGetAdminUser(String token) throws AuthenticationException {
        if (!JWTUtil.verify(token, secret.getBytes())) {
            throw new AuthenticationException("Token verification failed");
        }
        final JWT jwt = JWTUtil.parseToken(token);

        // 检查令牌是否过期
        Long expireAt = Convert.toLong(jwt.getHeader("expireAt"));
        if (expireAt < System.currentTimeMillis()) {
            throw new AuthenticationException("Token expired");
        }

        return jwt.getPayloads().toBean(AdminUserDTO.class);
    }
}
