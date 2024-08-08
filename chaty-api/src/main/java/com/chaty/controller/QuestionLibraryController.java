package com.chaty.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.entity.QuestionLibrary;
import com.chaty.service.QuestionLibraryService;

@RequestMapping("/api/questionLibrary")
@RestController
public class QuestionLibraryController {

    @Resource
    private QuestionLibraryService questionLibraryService;

    @GetMapping("/findAll")
    public BaseResponse<List<QuestionLibrary>> findAll(@ModelAttribute QuestionLibrary param) {
        List<QuestionLibrary> res = questionLibraryService.findAll(param);
        return BaseResponse.ok(res);
    }

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody QuestionLibrary param) {
        questionLibraryService.add(param);
        return BaseResponse.ok("新增成功");
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody QuestionLibrary param) {
        questionLibraryService.update(param);
        return BaseResponse.ok("更新成功");
    }

    @PostMapping("/delete")
    public BaseResponse<?> postMethodName(String id) {
        questionLibraryService.delete(id);
        return BaseResponse.ok("删除成功");
    }

    @GetMapping("findByKeyword")
    public BaseResponse<?> findByKeyword(String keyword, Integer searchType) {
        List<Map<String, ?>> res = questionLibraryService.findByKeyword(keyword, searchType);
        return BaseResponse.ok(res);
    }

}
