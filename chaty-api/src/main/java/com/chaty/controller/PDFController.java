package com.chaty.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.service.PDFService;
import com.chaty.service.PDFService.TexCmd;

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

    @PostMapping("/createDoc")
    public BaseResponse<?> createDoc(@RequestParam(defaultValue = TexCmd.XELATEX) String texCmd, 
            String template, @RequestBody Map<String, Object> params) {
        Map<String, Object> res = pdfService.createDoc(texCmd, template, params);
        return BaseResponse.ok(res);
    }

}
