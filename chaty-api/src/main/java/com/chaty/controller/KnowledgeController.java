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
import com.chaty.entity.Knowledge;
import com.chaty.service.KnowledgeService;

@RequestMapping("/api/knowledge")
@RestController
public class KnowledgeController {

    @Resource
    private KnowledgeService KnowledgeService;

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody Knowledge param) {
        KnowledgeService.add(param);
        return BaseResponse.ok("新增成功");
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody Knowledge param) {
        KnowledgeService.updateById(param);
        return BaseResponse.ok("更新成功");
    }

    @GetMapping("/list")
    public BaseResponse<List<Knowledge>> listByKeyword(String keyword, @ModelAttribute Knowledge param) {
        List<Knowledge> res = KnowledgeService.list(param, keyword);
        return BaseResponse.ok(res);
    }

    @PostMapping("/delete")
    public BaseResponse<?> delete(String id) {
        KnowledgeService.deleteById(id);
        return BaseResponse.ok("删除成功");
    }
}
