package com.chaty.security;

import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.chaty.dto.SchoolDTO;
import com.chaty.entity.Org;
import com.chaty.entity.User;

import cn.hutool.core.convert.Convert;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

public class AuthUtil {

    public static User getLoginUser() {
        Object principal = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> auth.getPrincipal()).orElse(null);
        if (Objects.nonNull(principal) && principal instanceof BaseUserDetails) {
            return ((BaseUserDetails) principal).getUser();
        }
        return null;
    }

    public static SchoolDTO getLoginSchool() {
        Object principal = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> auth.getPrincipal()).orElse(null);
        if (Objects.nonNull(principal) && principal instanceof BaseUserDetails) {
            return ((BaseUserDetails) principal).getSchool();
        }
        return null;
    }

    public static void setLoginSchool(SchoolDTO school) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = Optional.ofNullable(authentication)
                .map(auth -> auth.getPrincipal()).orElse(null);
        if (Objects.nonNull(principal) && principal instanceof BaseUserDetails) {
            ((BaseUserDetails) principal).setSchool(school);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    public static TokenAuth<?> getTokenAuth() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        return (TokenAuth) request.getAttribute(TokenAuthConsts.AUTH_KEY);
    }

    public static TokenAuth<Org> getOrgAuth() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        return Convert.convert(TokenAuth.class, request.getAttribute(TokenAuthConsts.AUTH_KEY));
    }

}
