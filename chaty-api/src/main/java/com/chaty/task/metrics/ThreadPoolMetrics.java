package com.chaty.task.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ThreadPoolMetrics {

    @Resource
    private TaskExecutor correctAreaTaskExecutor;
    @Resource
    private Executor docCorrectTaskExecutor;

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("areaCorrectTaskExecutor", getExecutorInfo(correctAreaTaskExecutor));
        metrics.put("docCorrectTaskExecutor", getExecutorInfo(docCorrectTaskExecutor));
        return metrics;
    }

    private Map<String, Object> getExecutorInfo(Executor executor) {
        if (!(executor instanceof ThreadPoolTaskExecutor)) {
            return Collections.emptyMap();
        }

        ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) executor;
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("poolSize", threadPoolTaskExecutor.getPoolSize());
        metrics.put("activeCount", threadPoolTaskExecutor.getActiveCount());
        metrics.put("corePoolSize", threadPoolTaskExecutor.getCorePoolSize());
        metrics.put("maxPoolSize", threadPoolTaskExecutor.getMaxPoolSize());
        metrics.put("queueSize", threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().size());
        metrics.put("largestPoolSize", threadPoolTaskExecutor.getThreadPoolExecutor().getLargestPoolSize());
        metrics.put("taskCount", threadPoolTaskExecutor.getThreadPoolExecutor().getTaskCount());
        metrics.put("completedTaskCount", threadPoolTaskExecutor.getThreadPoolExecutor().getCompletedTaskCount());
        return metrics;
    }

    /**
     * 定时输出线程池信息
     */
    @Scheduled(fixedRate = 60000)
    public void outputThreadPoolInfo() {
        Map<String, Object> metrics = getMetrics();
        log.info("ThreadPool Metrics: {}", metrics);
    }

}
