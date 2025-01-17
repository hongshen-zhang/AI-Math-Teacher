package com.chaty.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.cache.DocReviewCacheService;
import com.chaty.common.BaseResponse;
import com.chaty.dto.DocReviewDTO;
import com.chaty.dto.DocReviewDTO.AreaDTO;
import com.chaty.entity.DocReview;
import com.chaty.service.DocReviewService;

@RequestMapping("/api/docreview")
@RestController
public class DocReviewController {

    @Resource
    private DocReviewService docReviewService;
    @Resource
    private DocReviewCacheService docReviewCacheService;

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody DocReview param) {
        DocReview res = docReviewService.add(param);
        return BaseResponse.ok(res);
    }

    @PostMapping("/addBatch")
    public BaseResponse<?> addBatch(@RequestBody List<DocReview> params) {
        List<DocReview> res = docReviewService.addBatch(params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody DocReview param) {
        docReviewService.updateById(param);
        return BaseResponse.ok(null);
    }

    @GetMapping("/delete")
    public BaseResponse<?> delete(String id) {
        docReviewService.deleteById(id);
        return BaseResponse.ok("删除成功");
    }

    @PostMapping("/list")
    public BaseResponse<?> list(@RequestBody DocReview param) {
        List<DocReview> res = docReviewService.list(param);
        if (param.getSetProgress()) {
            res.forEach(doc -> {
                doc.setProgress(docReviewCacheService.get(doc.getId()));
            });
        }
        return BaseResponse.ok(res);
    }

    @PostMapping("/review")
    public BaseResponse<?> review(@RequestBody DocReviewDTO params) {
        Map<String, ?> res = docReviewService.review(params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/ocrCropArea")
    public BaseResponse<?> ocrCropArea(@RequestParam String docurl, @RequestBody AreaDTO area, @RequestParam(required = false) String service) {
        Map<String, Object> res = docReviewService.ocrCropArea(docurl, area, service);
        return BaseResponse.ok(res);
    }

    @PostMapping("/batchReviewDoc")
    public BaseResponse<?> batchReviewDoc(@RequestBody DocReviewDTO params) {
        Map<String, Object> res = docReviewService.batchReviewDoc(params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/reviewStatsDoc")
    public BaseResponse<?> reviewStatsDoc(@RequestBody DocReviewDTO params) {
        Map<String, Object> res = docReviewService.reviewStatsDoc(params);
        return BaseResponse.ok(res);
    }

}
