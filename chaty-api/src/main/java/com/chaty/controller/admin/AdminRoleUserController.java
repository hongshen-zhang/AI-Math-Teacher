package com.chaty.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.dto.AdminRoleUserDTO;
import com.chaty.service.admin.AdminRoleUserService;

@RequestMapping("/api/admin/role/user")
@RestController
public class AdminRoleUserController {

    @Resource
    private AdminRoleUserService adminRoleUserService;

    @PostMapping("/list")
    public BaseResponse<?> list(@RequestBody AdminRoleUserDTO params) {
        List<?> res = adminRoleUserService.list(params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody AdminRoleUserDTO params) {
        adminRoleUserService.add(params);
        return BaseResponse.ok("添加成功");
    }

    @GetMapping("/delete")
    public BaseResponse<?> delete(String id) {
        adminRoleUserService.delete(id);
        return BaseResponse.ok("删除成功");
    }

}
