package com.chaty.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.dto.ComprehensiveParamDTO;
import com.chaty.dto.ComprehensiveResultDTO;
import com.chaty.dto.DocCorrectConfigPackageDTO;
import com.chaty.entity.DocCorrectConfigPackage;
import com.chaty.service.ComprehensiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comprehensive")
public class DocCorrectComprehensiveController {

    @Autowired
    ComprehensiveService comprehensiveService;

    @PostMapping("/page")
    public BaseResponse<?> page(@RequestBody ComprehensiveParamDTO param) {
        IPage<ComprehensiveResultDTO> res = comprehensiveService.combinedPage(param);
        return BaseResponse.ok("查询成功", res);
    }
}
