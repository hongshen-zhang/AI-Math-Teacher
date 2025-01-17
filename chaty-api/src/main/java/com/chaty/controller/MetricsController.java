package com.chaty.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.task.metrics.TaskTimerMetrics;
import com.chaty.task.metrics.ThreadPoolMetrics;

@RequestMapping("/api/metrics")
@RestController
public class MetricsController {

    @Resource
    private TaskTimerMetrics recordTaskMetrics;

    @Resource
    private TaskTimerMetrics areaTaskMetrics;

    @Resource
    private ThreadPoolMetrics threadPoolMetrics;

    @GetMapping("/correct")
    public BaseResponse<?> getCorrectMetrics() {
        return BaseResponse.ok(recordTaskMetrics.getAvgTimeCache());
    }

    @GetMapping("/area")
    public BaseResponse<?> getAreaMetrics() {
        return BaseResponse.ok(areaTaskMetrics.getAvgTimeCache());
    }

    @GetMapping("/thread")
    public BaseResponse<?> getThreadMetrics() {
        return BaseResponse.ok(threadPoolMetrics.getMetrics());
    }

    @GetMapping("/clear/correct")
    public BaseResponse<?> clearCorrectMetrics() {
        recordTaskMetrics.clear();
        areaTaskMetrics.clear();
        return BaseResponse.ok("success");
    }

}
