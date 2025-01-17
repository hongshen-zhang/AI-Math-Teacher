package com.chaty.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.entity.DocQuestion;
import com.chaty.service.DocQuestionService;

@RequestMapping("/api/doc/question")
@RestController
public class DocQuestionController {

    @Resource
    private DocQuestionService docQuestionService;

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody DocQuestion param) {
        docQuestionService.add(param);
        return BaseResponse.ok("add success!");
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody DocQuestion param) {
        docQuestionService.updateById(param);
        return BaseResponse.ok("update success!");
    }

    @GetMapping("/delete")
    public BaseResponse<?> delete(Integer id) {
        docQuestionService.deleteById(id);
        return BaseResponse.ok("delete success!");
    }

    @PostMapping("/list")
    public BaseResponse<?> list(@RequestBody DocQuestion param) {
        return BaseResponse.ok(docQuestionService.list(param));
    }

    @GetMapping("/getById")
    public BaseResponse<?> getById(Integer id) {
        DocQuestion res = docQuestionService.selectById(id);
        return BaseResponse.ok(res);
    }

}
