package com.chaty.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.dto.UserDTO;
import com.chaty.entity.User;
import com.chaty.enums.UserRoleConsts;
import com.chaty.service.UserService;
import com.chaty.util.ExcelUtils;

@RequestMapping("/api/user")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody UserDTO user) {
        userService.insert(user);
        return BaseResponse.ok("add success");
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody User user) {
        userService.updateById(user);
        return BaseResponse.ok("update success");
    }

    @PostMapping("/update/nicknameAndStudentId")
    public BaseResponse<?> updateByNicknameAndStudentId(@RequestBody User user) {
        userService.updateByNicknameAndStudentId(user);
        return BaseResponse.ok("update success");
    }

    @GetMapping("/findById")
    public BaseResponse<User> findById(String id) {
        User res = userService.findById(id);
        return BaseResponse.ok(res);
    }

    @GetMapping("/list")
    public BaseResponse<List<User>> list(User params) {
        List<User> res = userService.list(params);
        return BaseResponse.ok(res);
    }

    @GetMapping("/delete")
    public BaseResponse<?> delete(String id) {
        userService.delete(id);
        return BaseResponse.ok("delete success");
    }

    @GetMapping("/user/count")
    public BaseResponse<?> userCount() {
        List<UserDTO> res = userService.userCount();
        return BaseResponse.ok(res);
    }

    @PostMapping("/teacher/add")
    public BaseResponse<?> addStudent(@RequestBody List<UserDTO> users) {
        for (UserDTO user : users) {
            user.setPassword(user.getStudentId());
            user.setRole(UserRoleConsts.STUDENT);
            user.setNickname(user.getNickname());
            user.setUsername(user.getUsername() + user.getStudentId());
            userService.insert(user);
        }
        return BaseResponse.ok("学生用户新增成功");
    }

    @PostMapping("/teacher/import/{classId}")
    public BaseResponse<?> importStudents(@RequestParam("file") MultipartFile file, @PathVariable("classId") String classId) throws IOException {
        List<UserDTO> users = ExcelUtils.readExcel(file);
        for (UserDTO user : users) {
            user.setPassword(user.getStudentId());
            user.setRole(UserRoleConsts.STUDENT);
            user.setNickname(user.getUsername());
            user.setUsername(user.getUsername() + user.getStudentId() + UUID.randomUUID().toString());
            user.setClassId(classId);
            userService.insert(user);
        }
        return BaseResponse.ok("学生用户批量导入成功");
    }

    @GetMapping("/student/list/{classId}")
    public BaseResponse<?> listByClassId(@PathVariable("classId") String classId) {
        List<User> users = userService.selectByClassId(classId);
        return BaseResponse.ok(users);
    }

    @PostMapping("/page")
    public BaseResponse<?> page(@RequestBody UserDTO params) {
        IPage<UserDTO> page = userService.findPage(params);
        return BaseResponse.ok(page);
    }
}
