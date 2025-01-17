package com.chaty.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaty.common.BaseResponse;
import com.chaty.dto.DocCorrectResultDTO;
import com.chaty.dto.DocCorrectTaskDTO;
import com.chaty.entity.User;
import com.chaty.security.AuthUtil;
import com.chaty.service.DocCorrectTaskService;
import com.chaty.service.PDFService;
import com.chaty.service.PDFService.TexCmd;
import com.chaty.task.correct.RecordCorrector;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;

@RequestMapping("/api/docCorrectTask")
@RestController
public class DocCorrectTaskController {

    @Resource
    private DocCorrectTaskService docCorrectTaskService;
    @Resource
    private PDFService pdfService;
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private RecordCorrector recordCorrector;

    @PostMapping("/page")
    public BaseResponse<?> page(@RequestBody DocCorrectTaskDTO param) {
        IPage<DocCorrectTaskDTO> res = docCorrectTaskService.page(param);
        return BaseResponse.ok("查询成功", res);
    }

    @PostMapping("/add")
    public BaseResponse<?> add(@RequestBody DocCorrectTaskDTO param) {
        docCorrectTaskService.add(param);
        return BaseResponse.ok("添加成功");
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@RequestBody DocCorrectTaskDTO param) {
        docCorrectTaskService.update(param);
        return BaseResponse.ok("修改成功");
    }

    @PostMapping("/delete")
    public BaseResponse<?> delete(String id) {
        docCorrectTaskService.delete(id);
        return BaseResponse.ok("删除成功");
    }

    @PostMapping("/execute")
    public BaseResponse<?> execute(@RequestBody DocCorrectTaskDTO param) {
        docCorrectTaskService.execute(param);
        return BaseResponse.ok("提交成功");
    }

    @PostMapping("/add/file")
    public BaseResponse<?> addByFile(@RequestBody DocCorrectTaskDTO param) {
        docCorrectTaskService.addByFile(param);
        return BaseResponse.ok("添加成功");
    }

    @PostMapping("/download/reviewed")
    public BaseResponse<?> downloadReviewed(@RequestBody DocCorrectTaskDTO param) {
        Map<String, Object> res = docCorrectTaskService.createReviewedDoc(param);
        return BaseResponse.ok(res);
    }

    @GetMapping("/status/stats")
    public BaseResponse<?> statsByStatus() {
        List<DocCorrectTaskDTO> res = docCorrectTaskService.statsByStatus();
        return BaseResponse.ok(res);
    }

    @GetMapping("/get/{id}")
    public BaseResponse<?> getById(@PathVariable String id) {
        DocCorrectTaskDTO res = docCorrectTaskService.getById(id);
        return BaseResponse.ok(res);
    }

    @PostMapping("/download/stats")
    public BaseResponse<?> downloadStats(@RequestBody DocCorrectTaskDTO param) {
        Map<String, Object> res = docCorrectTaskService.createStatsDoc(param);
        return BaseResponse.ok(res);
    }

    @PostMapping("/wrongQs/doc")
    public BaseResponse<?> createWrongQsDoc(@RequestBody List<String> imgs) {
        User user = AuthUtil.getLoginUser();
        Map<String, Object> params = MapUtil.builder(new HashMap<String, Object>())
            .put("nickname", user.getNickname())
            .put("imgs", imgs)
            .put("date", DateUtil.format(LocalDateTime.now(), "yyyyMMdd"))
            .build();
        Map<String, Object> res = pdfService.createDoc(TexCmd.PDFLATEX, "wrongQs", params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/correctName")
    public BaseResponse<?> correctDocName(@RequestBody DocCorrectTaskDTO param) {
        taskExecutor.execute(() -> {
            recordCorrector.correctDocName(param);
        });
        return BaseResponse.ok("校正中，请稍后查看...");
    }

    @PostMapping("/syncDocName")
    public BaseResponse<?> syncDocName(@RequestBody DocCorrectTaskDTO param) {
        docCorrectTaskService.syncDocName(param);
        return BaseResponse.ok("同步成功");
    }

    @GetMapping("/correct/count")
    public BaseResponse<?> correctCount() {
        Map<String, Object> res = docCorrectTaskService.correctCount();
        return BaseResponse.ok(res);
    }

    @GetMapping("/task/stats")
    public BaseResponse<?> taskStats(String taskId) {
        Map<String, Object> res = docCorrectTaskService.taskStats(taskId);
        return BaseResponse.ok(res);
    }

    @PostMapping("/tasks/stats")
    public BaseResponse<?> tasksStats(@RequestBody List<String> taskIds) {
        List<Map<String, Object>> res = docCorrectTaskService.tasksStats(taskIds);
        return BaseResponse.ok(res);
    }

    @GetMapping("/qs/stats")
    public BaseResponse<?> qsStats(String taskId, String configId) {
        Map<Integer, Map<Integer, DocCorrectResultDTO>> res = docCorrectTaskService.qsStats(taskId, configId);
        return BaseResponse.ok(res);
    }

    @PostMapping("/file/zip")
    public BaseResponse<?> resZip(@RequestBody DocCorrectTaskDTO param) {
        Map<String, Object> res = docCorrectTaskService.fileZip(param);
        return BaseResponse.ok(res);
    }
}
