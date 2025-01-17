package com.chaty.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chaty.common.BaseResponse;
import com.chaty.service.FileService;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

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

    @PostMapping("/upload/doc2img")
    public BaseResponse<?> uploadDoc2Img(@RequestParam("file") MultipartFile file, boolean checkpageNum) {
        Map<String, Object> res = fileService.doc2img(file, checkpageNum);
        return BaseResponse.ok("上传文件成功", res);
    }

    @PostMapping("/upload/multiPdf")
    public BaseResponse<?> uploadMulitePdf(@RequestParam("file") MultipartFile file, 
        @RequestParam(value = "config", required = false) String config) {
        JSONObject configObj = null;
        if (Objects.nonNull(config)) {
            configObj = JSONUtil.parseObj(config);
        }
        List<List<Map<String, Object>>> uploadRes = fileService.uploadMulitePdf(file, configObj);
        Object res = uploadRes;
        if (Objects.isNull(config)) {
            res = uploadRes.get(0);
        }
        return BaseResponse.ok("上传文件成功", res);
    }

    @GetMapping("/pdf2Img")
    public BaseResponse<?> pdf2Img(@RequestParam String filename, @RequestParam Integer pageNum) {
        Map<String, Object> res = fileService.pdf2Img(filename, pageNum);
        return BaseResponse.ok(res);
    }

}
