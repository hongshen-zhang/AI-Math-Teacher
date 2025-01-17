package com.chaty.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.completion.CompletionService;
import com.chaty.dto.CorrectQsDTO;
import com.chaty.dto.DocCorrectConfigDTO;
import com.chaty.entity.DocCorrectConfig;
import com.chaty.form.ExtraQsForm;
import com.chaty.service.DocCorrectConfigService;

import cn.hutool.core.map.MapUtil;

@RequestMapping("/api/docCorrectConfig")
@RestController
public class DocCorrectConfigController {

    @Resource
    private DocCorrectConfigService docCorrectConfigService;

    @Resource
    private CompletionService completionService;

    @PostMapping("/page")
    public BaseResponse<?> page(@RequestBody DocCorrectConfigDTO param) {
        IPage<DocCorrectConfig> res = docCorrectConfigService.page(param);
        return BaseResponse.ok("查询成功", res);
    }

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody DocCorrectConfigDTO param) {
        String id = docCorrectConfigService.add(param);
        return BaseResponse.ok("添加成功", MapUtil.of("id", id));
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody DocCorrectConfigDTO param) {
        String id = docCorrectConfigService.update(param);
        return BaseResponse.ok("修改成功", MapUtil.of("id", id));
    }

    @PostMapping("/delete")
    public BaseResponse<?> delete(String id) {
        docCorrectConfigService.delete(id);
        return BaseResponse.ok("删除成功");
    }

    @GetMapping("/get")
    public BaseResponse<?> getById(String id) {
        DocCorrectConfig res = docCorrectConfigService.getById(id);
        return BaseResponse.ok(res);
    }

    @PostMapping("/extraQs")
    public BaseResponse<?> extraQs(@RequestBody ExtraQsForm form) {
        List<CorrectQsDTO> res = completionService.extraQs(form);
        return BaseResponse.ok(res);
    }

}
