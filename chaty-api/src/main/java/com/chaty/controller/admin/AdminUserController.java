package com.chaty.controller.admin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.dto.AdminUserDTO;
import com.chaty.entity.admin.AdminUser;
import com.chaty.security.JwtTokenService;
import com.chaty.service.admin.AdminUserService;

import cn.hutool.core.bean.BeanUtil;

@RequestMapping("/api/admin/user")
@RestController
public class AdminUserController {

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private AdminUserService adminUserService;

    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody AdminUserDTO params, HttpServletResponse response) {
        AdminUser user = adminUserService.login(params);
        AdminUserDTO res = BeanUtil.copyProperties(user, AdminUserDTO.class);
        res.setPassword(null);
        res.setAccessToken(jwtTokenService.generateAdminToken(user));
        return BaseResponse.ok(res);
    }

    @GetMapping("/list")
    public BaseResponse<?> list() {
        return BaseResponse.ok("list success");
    }

}
