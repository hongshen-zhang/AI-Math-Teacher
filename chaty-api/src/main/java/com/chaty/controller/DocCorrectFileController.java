package com.chaty.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.dto.DocCorrectFileDTO;
import com.chaty.service.DocCorrectFileService;

@RestController
@RequestMapping("/api/docCorrectFile")
public class DocCorrectFileController {

    @Resource
    private DocCorrectFileService docCorrectFileService;


    @PostMapping("/page")
    public BaseResponse<?> page(@RequestBody DocCorrectFileDTO param) {
        return BaseResponse.ok(docCorrectFileService.page(param));
    }

    @PostMapping("/create")
    public BaseResponse<?> create(@RequestBody DocCorrectFileDTO param) {
        DocCorrectFileDTO correctFile = docCorrectFileService.createFile(param);
        return BaseResponse.ok(correctFile);
    }

    @PostMapping("/correct")
    public BaseResponse<?> correct(@RequestBody DocCorrectFileDTO param) {
        docCorrectFileService.doCorrect(param);
        return BaseResponse.ok("已提交批改");
    }

    @PostMapping("/delete")
    public BaseResponse<?> delete(String id) {
        docCorrectFileService.delete(id);
        return BaseResponse.ok("删除成功");
    }
    
    @GetMapping("/getById")
    public BaseResponse<?> getById(String id) {
        DocCorrectFileDTO res = docCorrectFileService.getById(id);
        return BaseResponse.ok(res);
    }

}
