package com.chaty.controller;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.service.OCRService;

@RequestMapping("/api/ocr")
@RestController
public class OCRController {

    @Resource
    private Map<String, OCRService> ocrServices;

    @PostMapping("/url2text")
    public BaseResponse<String> ocrForText(@RequestParam String url, @RequestParam(required = false) String service) {
        OCRService ocrService = Optional.ofNullable(service).map(ocrServices::get).orElse(ocrServices.get("tencentOCRService"));
        String res = ocrService.ocrForText(url);
        return BaseResponse.ok("success", res);
    }

}
