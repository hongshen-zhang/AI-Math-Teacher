package com.chaty.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.service.PDFService;

@RequestMapping("/api/pdf")
@RestController
public class PDFController {

    @Resource
    private PDFService pdfService;

    @PostMapping("/createPDF")
    public BaseResponse<?> createPDF(@RequestBody Map<String, Object> params) {
        Map<String, Object> res = pdfService.createPDF(params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/solveAll")
    public BaseResponse<?> solveAllPDF(@RequestBody Map<String, Object> params) {
        Map<String, Object> res = pdfService.solveAllPDF(params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/coursenote")
    public BaseResponse<?> coursenote(@RequestBody Map<String, Object> params) {
        Map<String, Object> res = pdfService.coursenotePDF(params);
        return BaseResponse.ok(res);
    }

}
