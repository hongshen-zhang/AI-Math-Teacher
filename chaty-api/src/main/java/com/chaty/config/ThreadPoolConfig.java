package com.chaty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ThreadPoolConfig {

    @Value("${task.correctArea.corePoolSize:5}")
    private int correctAreaCorePoolSize;
    @Value("${task.correctArea.maxPoolSize:10}")
    private int correctAreaMaxPoolSize;
    /**
     * 任务数量
     */
    @Value("${doc-correct.task-num:5}")
    public Integer correctRecordTaskNum;
    /**
     * 队列大小
     */
    @Value("${doc-correct.queue-size:100}")
    public Integer correctRecordQueueSize;

    @Bean("taskExecutor")
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean("correctAreaTaskExecutor")
    public TaskExecutor correctAreaTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(correctAreaCorePoolSize);
        executor.setMaxPoolSize(correctAreaMaxPoolSize);
        executor.setThreadNamePrefix("correctAreaTaskExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor docCorrectTaskExecutor() {
        log.info("DocCorrectTask init, taskNum: {}, queueSize: {}", correctRecordTaskNum, correctRecordQueueSize);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(correctRecordTaskNum);
        executor.setMaxPoolSize(correctRecordTaskNum);
        executor.setKeepAliveSeconds(0);
        executor.setQueueCapacity(correctRecordQueueSize);
        executor.setThreadFactory(r -> {
            Thread t = new Thread(r, "DocCorrectTask");
            t.setPriority(Thread.NORM_PRIORITY + 1);
            return t;
        });
        executor.initialize();
        return executor;
    }

}
