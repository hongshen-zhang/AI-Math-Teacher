package com.chaty.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.entity.DocFileConfig;
import com.chaty.service.DocFileConfigService;

@RequestMapping("/api/docFileConfig")
@RestController
public class DocFileConfigController {

    @Resource
    private DocFileConfigService docFileConfigService;

    @GetMapping("/getByCode")
    public BaseResponse<?> getByCode(String code) {
        DocFileConfig res = docFileConfigService.getByCode(code);
        return BaseResponse.ok(res);
    }
    
}
