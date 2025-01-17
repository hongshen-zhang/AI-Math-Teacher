package com.chaty.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.entity.DocReviewRec;
import com.chaty.service.DocReviewRecService;

@RequestMapping("/api/docreviewrec")
@RestController
public class DocReviewRecController {
    
    @Resource
    private DocReviewRecService docReviewRecService;

    @PostMapping("/batch/add")
    public BaseResponse<?> batchAdd(@RequestBody DocReviewRec params) {
        docReviewRecService.batchAdd(params);
        return BaseResponse.ok(null);
    }

    @PostMapping("/page")
    public BaseResponse<?> findPage(@RequestBody DocReviewRec params) {
        IPage<DocReviewRec> res = docReviewRecService.findPage(params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/delete/{id}")
    public BaseResponse<?> deleteById(@PathVariable String id) {
        docReviewRecService.deleteById(id);
        return BaseResponse.ok("delete success");
    }

}
