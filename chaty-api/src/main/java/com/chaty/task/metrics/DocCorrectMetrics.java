package com.chaty.task.metrics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DocCorrectMetrics {

    @Value("${metrics.record.size:10}")
    public Integer recordMetricsSize;

    @Value("${metrics.area.size:20}")
    public Integer areaMetricsSize;

    @Bean
    public TaskTimerMetrics recordTaskMetrics() {
        return new TaskTimerMetrics(recordMetricsSize);
    }

    @Bean
    public TaskTimerMetrics areaTaskMetrics() {
        return new TaskTimerMetrics(areaMetricsSize);
    }

}
