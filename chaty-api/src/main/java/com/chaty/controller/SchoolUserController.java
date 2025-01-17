package com.chaty.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.dto.SchoolUserDTO;
import com.chaty.service.SchoolUserService;

@RequestMapping("/api/school/user")
@RestController
public class SchoolUserController {

    @Resource
    private SchoolUserService schoolUserService;

    @PostMapping("/add")
    public BaseResponse<?> addSchoolUser(@RequestBody SchoolUserDTO params) {
        schoolUserService.add(params);
        return BaseResponse.ok("添加成功");
    }

    @GetMapping("/delete")
    public BaseResponse<?> deleteSchoolUser(String id) {
        schoolUserService.deleteById(id);
        return BaseResponse.ok("删除成功");
    }

    @PostMapping("/findPage")
    public BaseResponse<?> findPage(@RequestBody SchoolUserDTO params) {
        return BaseResponse.ok(schoolUserService.findPage(params));
    }

}
