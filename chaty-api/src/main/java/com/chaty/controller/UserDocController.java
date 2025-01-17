package com.chaty.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.dto.DocCorrectRecordDTO;
import com.chaty.dto.DocCorrectTaskDTO;
import com.chaty.dto.UserDocDTO;
import com.chaty.service.UserDocService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/userdoc")
public class UserDocController {

    @Resource
    private UserDocService userDocService;

    @PostMapping("/assign")
    public BaseResponse<?> assign(@RequestBody UserDocDTO params) {
        userDocService.assignedDocs(params);
        return BaseResponse.ok("assigned success");
    }

    @PostMapping("/tasks")
    public BaseResponse<?> loadUserTasks(@RequestBody DocCorrectTaskDTO param) {
        IPage<DocCorrectTaskDTO> res = userDocService.getUserTask(param);
        return BaseResponse.ok("查询成功", res);
    }

    @PostMapping("/docs")
    public BaseResponse<?> loadUserDocs(@RequestBody DocCorrectRecordDTO param) {
        IPage<DocCorrectRecordDTO> res = userDocService.getUserDoc(param);
        return BaseResponse.ok("查询成功", res);
    }

    @GetMapping("/taskIds")
    public BaseResponse<?> loadTaskIds(String userId) {
        List<String> res = userDocService.getTaskIds(userId);
        return BaseResponse.ok("查询成功", res);
    }

    @GetMapping("/docIds")
    public BaseResponse<?> loadTaskIds(String userId, String taskId) {
        List<String> res = userDocService.getDocIds(userId, taskId);
        return BaseResponse.ok("查询成功", res);
    }

    @PostMapping("/delete")
    public BaseResponse<?> deleteDocs(@RequestBody UserDocDTO params) {
        userDocService.deleteUserDocs(params);
        return BaseResponse.ok("");
    }

    /**
     * 根据用户名自动分配试卷
     */
    @PostMapping("/autoAssign")
    public BaseResponse<?> autoAssign(@RequestBody UserDocDTO params) {
        userDocService.autoAssign(params);
        return BaseResponse.ok("success");
    }

}
