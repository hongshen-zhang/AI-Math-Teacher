package com.chaty.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.dto.DocCorrectConfigPackageDTO;
import com.chaty.entity.DocCorrectConfigPackage;
import com.chaty.service.DocCorrectConfigPackageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/api/docCorrectConfigPackage")
@RestController
public class DocCorrectConfigPackageController {

    @Resource
    private DocCorrectConfigPackageService docCorrectConfigPackageService;

    @PostMapping("/page")
    public BaseResponse<?> page(@RequestBody DocCorrectConfigPackageDTO param) {
        IPage<DocCorrectConfigPackage> res = docCorrectConfigPackageService.page(param);
        return BaseResponse.ok("查询成功", res);
    }

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody DocCorrectConfigPackageDTO param) {
        String id = docCorrectConfigPackageService.add(param);
        return BaseResponse.ok("添加成功", MapUtil.of("id", id));
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody DocCorrectConfigPackageDTO param) {
        String id = docCorrectConfigPackageService.update(param);
        return BaseResponse.ok("修改成功", MapUtil.of("id", id));
    }

    @PostMapping("/delete")
    public BaseResponse<?> delete(String id) {
        docCorrectConfigPackageService.delete(id);
        return BaseResponse.ok("删除成功");
    }

    @GetMapping("/get")
    public BaseResponse<?> getById(String id) {
        DocCorrectConfigPackage res = docCorrectConfigPackageService.getById(id);
        return BaseResponse.ok(res);
    }
}
