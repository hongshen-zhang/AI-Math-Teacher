package com.chaty.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.service.AuthService;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    @Resource
    private AuthService authService;

    @GetMapping("/getAuthData")
    public BaseResponse<?> getAuthData() {
        return BaseResponse.ok(authService.getAuthData());
    }

    @GetMapping("/login/school")
    public BaseResponse<?> loginWithSchool(String schoolId) {
        authService.loginWithSchool(schoolId);
        return BaseResponse.ok("login success");
    }

    @GetMapping("/getSchools")
    public BaseResponse<?> getSchools() {
        return BaseResponse.ok(authService.getSchools());
    }

}
