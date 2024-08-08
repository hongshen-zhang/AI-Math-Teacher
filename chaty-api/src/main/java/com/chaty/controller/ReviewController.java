package com.chaty.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.entity.Review;
import com.chaty.service.ReviewService;

@RequestMapping("/api/review")
@RestController
public class ReviewController {

    @Resource
    private ReviewService reviewService;

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody Review param) {
        reviewService.insertOne(param);
        return BaseResponse.ok("新增成功");
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody Review param) {
        reviewService.updateById(param);
        return BaseResponse.ok("更新成功");
    }

    @GetMapping("/list")
    public BaseResponse<List<Review>> listByKeyword(String keyword, @ModelAttribute Review param) {
        List<Review> res = reviewService.list(param, keyword);
        return BaseResponse.ok(res);
    }

    @PostMapping("/delete")
    public BaseResponse<?> delete(String id) {
        reviewService.deleteById(id);
        return BaseResponse.ok("删除成功");
    }

}
