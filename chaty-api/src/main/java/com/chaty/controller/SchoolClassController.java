package com.chaty.controller;


import com.chaty.common.BaseResponse;
import com.chaty.entity.SchoolClass;
import com.chaty.service.SchoolClassService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/class")
public class SchoolClassController {

    @Resource
    private SchoolClassService schoolClassService;

    @GetMapping("/all")
    public BaseResponse<?> getAllClasses() {
        List<SchoolClass> schoolClasses = schoolClassService.selectAll();
        return BaseResponse.ok(schoolClasses);
    }

    @PostMapping("/addByBatch")
    public BaseResponse<?> addClass(@RequestBody List<SchoolClass> schoolClasses) {
        schoolClasses = schoolClassService.addBatch(schoolClasses);
        return BaseResponse.ok(schoolClasses);
    }

    @PutMapping("/update/{id}")
    public BaseResponse<?> updateClass(@PathVariable String id, @RequestBody SchoolClass schoolClass) {
        schoolClass.setClassId(id);
        boolean result = schoolClassService.classUpdateById(schoolClass);
        return BaseResponse.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse<?> deleteClass(@PathVariable String id) {
        Integer result = schoolClassService.deleteById(id);
        return BaseResponse.ok(result);
    }

    @PostMapping("/delete")
    public BaseResponse<?> deleteClasses(@RequestBody List<String> ids) {
        Integer result = schoolClassService.deleteByBatch(ids);
        return BaseResponse.ok(result);
    }

    @GetMapping("/by-school/{schoolId}")
    public BaseResponse<?> getClassesBySchool(@PathVariable String schoolId) {
        List<SchoolClass> schoolClasses = schoolClassService.selectBySchool(schoolId);
        return BaseResponse.ok(schoolClasses);
    }

}

