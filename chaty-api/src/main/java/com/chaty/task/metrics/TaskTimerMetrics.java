package com.chaty.task.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TaskTimerMetrics {

    private int windowSize; // 任务统计长度
    private AtomicInteger counter = new AtomicInteger(0); // 任务计数器
    private List<TaskTimer> timers; // 任务计时器列表
    private final ReentrantLock lock = new ReentrantLock(); // 可重入锁，用于线程安全地访问缓存
    private Map<String, Long> avgTimeCache = new ConcurrentHashMap<>(); // 缓存
    private Map<String, Long> timerKeyCount = new ConcurrentHashMap<>(); // 缓存

    public TaskTimerMetrics() {
        this(10);
    }

    public TaskTimerMetrics(int windowSize) {
        this.windowSize = windowSize;
        timers = new ArrayList<>(windowSize);
    }

    public TaskTimer createTimer(String id) {
        TaskTimer timer = new TaskTimer(id);
        timer.start();
        return timer;
    }

    public void closeTimer(TaskTimer taskTimer) {
        taskTimer.stop();
        List<TaskTimer> currentTimers = null;
        try {
            lock.lock();
            timers.add(taskTimer);
            if (counter.incrementAndGet() % windowSize == 0) {
                currentTimers = new ArrayList<>(timers);
                timers.clear(); // 清空缓存中的计时器
            }
        } finally {
            lock.unlock();
        }
        // 如果当前计时器数量达到窗口大小，计算并输出窗口内的平均耗时
        if (currentTimers != null) {
            log.info("Calculating metrics for {} tasks", currentTimers.size());
            calculateMetrics(currentTimers);
        }
    }

    private void calculateMetrics(List<TaskTimer> timers) {
        Map<String, Long> stepMap = new HashMap<>();
        Map<String, Long> stepCountMap = new HashMap<>();

        timers.parallelStream().map(timer -> {
            Map<String, Long> map = timer.getStepMap();
            map.put("time", timer.getDuration());
            return map;
        }).forEach(current -> {
            current.forEach((key, value) -> {
                stepMap.put(key, stepMap.getOrDefault(key, 0l) + current.get(key));
                stepCountMap.put(key, stepCountMap.getOrDefault(key, 0l) + 1);
            });
        });
        // 合并缓存任务执行平均时间
        stepMap.forEach((key, value) -> {
            long windowCount = stepCountMap.get(key);
            long cacheCount = timerKeyCount.getOrDefault(key, 0l);
            long cacheTotal = avgTimeCache.getOrDefault(key, 0l) * cacheCount;
            long total = cacheTotal + value;
            long avg = total / (cacheCount + windowCount);
            avgTimeCache.put(key, avg);
            timerKeyCount.put(key, cacheCount + windowCount);
        });
    }

    public Map<String, Long> getAvgTimeCache() {
        return avgTimeCache;
    }

    public void clear() {
        avgTimeCache.clear();
        timerKeyCount.clear();
    }

}