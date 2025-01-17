package com.chaty.controller.admin;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.dto.AdminRoleDTO;
import com.chaty.service.admin.AdminRoleService;

@RequestMapping("/api/admin/role")
@RestController
public class AdminRoleController {

    @Resource
    private AdminRoleService adminRoleService;

    @PostMapping("/page")
    public BaseResponse<?> getPage(@RequestBody AdminRoleDTO params) {
        IPage<?> res = adminRoleService.getPage(params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody AdminRoleDTO params) {
        adminRoleService.add(params);
        return BaseResponse.ok("添加成功");
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody AdminRoleDTO params) {
        adminRoleService.update(params);
        return BaseResponse.ok("更新成功");
    }

    @GetMapping("/delete")
    public BaseResponse<?> delete(String id) {
        adminRoleService.delete(id);
        return BaseResponse.ok("删除成功");
    }

}
