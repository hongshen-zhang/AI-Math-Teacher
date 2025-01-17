package com.chaty.task.metrics;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * 任务计时器
 */
@Slf4j
public class TaskTimer {
    private String id;
    private long start;
    private long end;
    private long timer;
    private Map<String, Long> steps = new HashMap<>();

    public TaskTimer(String id) {
        this.id = id;
    }

    public void start() {
        log.info("Task {} started", id);
        this.timer = this.start = System.currentTimeMillis();
    }

    public void step(String stepName) {
        step(stepName, false);
    }

    public void step(String stepName, boolean isEnd) {
        long time =  System.currentTimeMillis() - this.timer;
        log.info("Task {} step {} took {} ms", id, stepName, time);
        steps.put(stepName, time);
        this.timer = System.currentTimeMillis();
        if (isEnd) {
            stop();
        }
    }

    public void stop() {
        log.info("Task {} stopped", id);
        this.end = System.currentTimeMillis();
    }

    public long getDuration() {
        return this.end - this.start;
    }

    public String getId() {
        return id;
    }

    public Map<String, Long> getStepMap() {
        return steps;
    }
}
