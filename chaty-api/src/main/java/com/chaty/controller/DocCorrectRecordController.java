package com.chaty.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.dto.DocCorrectRecordDTO;
import com.chaty.service.DocCorrectRecordService;

@RequestMapping("/api/docCorrectRecord")
@RestController
public class DocCorrectRecordController {

    private static final Log log = LogFactory.getLog(DocCorrectRecordController.class);
    @Resource
    public DocCorrectRecordService docCorrectRecordService;

    @PostMapping("/page")
    public BaseResponse<?> page(@RequestBody DocCorrectRecordDTO param) {
        IPage<DocCorrectRecordDTO> res = docCorrectRecordService.page(param);
        return BaseResponse.ok("查询成功", res);
    }

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody DocCorrectRecordDTO param) {
        docCorrectRecordService.add(param);
        return BaseResponse.ok("添加成功");
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody DocCorrectRecordDTO param) {
        docCorrectRecordService.update(param);
        return BaseResponse.ok("修改成功");
    }

    @PostMapping("/delete")
    public BaseResponse<?> delete(String id) {
        docCorrectRecordService.delete(id);
        return BaseResponse.ok("删除成功");
    }

    @PostMapping("/download/reviewed")
    public BaseResponse<?> downloadReviewed(@RequestBody DocCorrectRecordDTO param) {
        Map<String, Object> res = docCorrectRecordService.createReviewedDoc(param);
        return BaseResponse.ok(res);
    }

    @PostMapping("/download/essayReport")
    public BaseResponse<?> downloadEssayReport(@RequestBody DocCorrectRecordDTO param) {
        Map<String, Object> res = docCorrectRecordService.createEssayReport(param);
        return BaseResponse.ok(res);
    }

    @PostMapping("/download/essayAnalyticalReport")
    public BaseResponse<?> essayAnalyticalReport(@RequestBody DocCorrectRecordDTO param) {
        Map<String, Object> res = docCorrectRecordService.createEssayAnalyticalReport(param);
        return BaseResponse.ok(res);
    }

    /**
     * 一键纠正
     * @param taskId
     * @return
     */
    @GetMapping("/qs/correctbyBatch")
    public BaseResponse<?> correctQs(String taskId, Boolean isUpdateHasChanged, Integer qsIdx, Boolean isRight) {
        // 打印入参
        log.info("taskId: " + taskId + ", isUpdateHasChanged: " + isUpdateHasChanged + ", qsIdx: " + qsIdx + ", isRight: " + isRight);
        Integer cnt = docCorrectRecordService.correctQs(taskId, isUpdateHasChanged, qsIdx, isRight);
        return BaseResponse.ok("批改成功", cnt);
    }
}