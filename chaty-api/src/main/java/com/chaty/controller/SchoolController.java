package com.chaty.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.dto.SchoolDTO;
import com.chaty.entity.School;
import com.chaty.service.SchoolService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/school")
public class SchoolController {

    @Resource
    private SchoolService schoolService;

    @GetMapping("/all")
    public BaseResponse<?> getAllSchools() {
        List<School> schools = schoolService.selectAll();
        return BaseResponse.ok(schools);
    }

    @PostMapping("/add")
    public BaseResponse<?> addSchool(@RequestBody SchoolDTO school) {
        Integer result = schoolService.add(school);
        return BaseResponse.ok(result);
    }

    // 更新学校
    @PostMapping("/update")
    public BaseResponse<?> updateSchool(@RequestBody SchoolDTO school) {
        Integer result = schoolService.updateById(school);
        return BaseResponse.ok(result);
    }

    // 删除学校（根据 ID）
    @DeleteMapping("/delete/{id}")
    public BaseResponse<?> deleteSchool(@PathVariable String id) {
        Integer result = schoolService.deleteById(id);
        return BaseResponse.ok(result);
    }

    // 批量删除学校
    // @DeleteMapping("/delete")
    public BaseResponse<?> deleteSchools(@RequestBody List<String> ids) {
        Integer result = schoolService.deleteByBatch(ids);
        return BaseResponse.ok(result);
    }

    @PostMapping("/findPage")
    public BaseResponse<?> findPage(@RequestBody SchoolDTO params) {
        IPage<?> page = schoolService.findPage(params);
        return BaseResponse.ok(page);
    }
}
