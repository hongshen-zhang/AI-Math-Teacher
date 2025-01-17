package com.chaty.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.dto.DocTagDTO;
import com.chaty.service.DocTagService;

@RequestMapping("/api/doc/tag")
@RestController
public class DocTagController {

    @Resource
    private DocTagService docTagService;
    
    @PostMapping("/add")
    public BaseResponse<?> addTag(@RequestBody DocTagDTO params) {
        docTagService.addTag(params);
        return BaseResponse.ok("新增成功");
    }

    @PostMapping("/delete")
    public BaseResponse<?> updateTag(@RequestBody DocTagDTO params) {
        docTagService.deleteTag(params);
        return BaseResponse.ok("");
    }

    @PostMapping("/doc/tags")
    public BaseResponse<?> listByDocs(@RequestBody DocTagDTO params) {
        return BaseResponse.ok(docTagService.listByDocs(params));
    }

    @GetMapping("/tags")
    public BaseResponse<?> list(@ModelAttribute DocTagDTO params) {
        return BaseResponse.ok(docTagService.list(params));
    }

    @PostMapping("/set")
    public BaseResponse<?> setTag(@RequestBody DocTagDTO params) {
        docTagService.setTag(params);
        return BaseResponse.ok("设置成功");
    }

    @PostMapping("/deleteTag")
    public BaseResponse<?> deleteDocTag(@RequestBody DocTagDTO params) {
        docTagService.deleteDocTag(params);
        return BaseResponse.ok("删除成功");
    }

}
