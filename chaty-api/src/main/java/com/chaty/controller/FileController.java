package com.chaty.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chaty.common.BaseResponse;
import com.chaty.service.FileService;

@RequestMapping("/api/file")
@RestController
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public BaseResponse<?> upload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> res = fileService.saveFile(file);
        return BaseResponse.ok("上传文件成功", res);
    }

}
