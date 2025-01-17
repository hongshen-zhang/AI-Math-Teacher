package com.chaty.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.dto.DocCorrectResultDTO;
import com.chaty.service.DocCorrectResultService;

@RequestMapping("/api/docCorrectResult")
@RestController
public class DocCorrectResultController {

    @Resource
    private DocCorrectResultService docCorrectResultService;
    
    @PostMapping("/saveRecord")
    public BaseResponse<?> saveRecord(String recordId) {
        docCorrectResultService.saveByRecordId(recordId);
        return BaseResponse.ok("保存成功");
    }

    @PostMapping("/saveTask")
    public BaseResponse<?> saveTask(String taskId) {
        docCorrectResultService.saveByTask(taskId);
        return BaseResponse.ok("保存成功");
    }

    @PostMapping("/page")
    public BaseResponse<?> results(@RequestBody DocCorrectResultDTO param) {
        IPage<DocCorrectResultDTO> res = docCorrectResultService.page(param);
        return BaseResponse.ok("查询成功", res);
    }

}
