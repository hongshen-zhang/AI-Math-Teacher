package com.chaty.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.dto.DocReviewedDTO;
import com.chaty.entity.DocReviewed;
import com.chaty.service.DocReviewedService;

@RequestMapping("/api/doc/reviewed")
@RestController
public class DocReviewedController {

    @Resource
    private DocReviewedService docReviewedService;

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody DocReviewed param) {
        DocReviewed res = docReviewedService.add(param);
        return BaseResponse.ok(res);
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody DocReviewed param) {
        docReviewedService.updateById(param);
        return BaseResponse.ok("update success!");
    }

    @GetMapping("/delete")
    public BaseResponse<?> delete(Integer id) {
        docReviewedService.deleteById(id);
        return BaseResponse.ok("delete success!");
    }

    @PostMapping("/list")
    public BaseResponse<?> list(@RequestBody DocReviewed param) {
        return BaseResponse.ok(docReviewedService.list(param));
    }

    @GetMapping("/getById")
    public BaseResponse<?> getById(Integer id) {
        DocReviewed res = docReviewedService.selectById(id);
        return BaseResponse.ok(res);
    }

    @PostMapping("/review")
    public BaseResponse<?> review(@RequestBody DocReviewedDTO param) {
        Map<String, Object> res = docReviewedService.review(param);
        return BaseResponse.ok(res);
    }

    @PostMapping("/batchReviewDoc")
    public BaseResponse<?> batchReviewDoc(@RequestBody DocReviewedDTO param) {
        Map<String, Object> res = docReviewedService.batchReviewDoc(param);
        return BaseResponse.ok(res);
    }
    

}
