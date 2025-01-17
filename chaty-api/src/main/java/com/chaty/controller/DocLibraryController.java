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

import com.chaty.common.BaseResponse;
import com.chaty.entity.DocLibrary;
import com.chaty.service.DocLibraryService;

@RequestMapping("/api/doclibrary")
@RestController
public class DocLibraryController {

    @Resource
    private DocLibraryService docLibraryService;

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody DocLibrary params) {
        Map<String, Object> res = docLibraryService.add(params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody DocLibrary params) {
        docLibraryService.update(params);
        return BaseResponse.ok(null);
    }

    @PostMapping("/list")
    public BaseResponse<?> list(@RequestBody DocLibrary params) {
        List<DocLibrary> res = docLibraryService.list(params);
        return BaseResponse.ok(res);
    }

    @GetMapping("/delete")
    public BaseResponse<?> delete(@RequestParam String id) {
        docLibraryService.delete(id);
        return BaseResponse.ok(null);
    }

    @GetMapping("/getById")
    public BaseResponse<?> getById(@RequestParam String id) {
        DocLibrary res = docLibraryService.getById(id);
        return BaseResponse.ok(res);
    }

}
