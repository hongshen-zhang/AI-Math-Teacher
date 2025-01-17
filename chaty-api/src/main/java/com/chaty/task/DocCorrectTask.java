package com.chaty.task;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.chaty.tenant.IgnoreTenant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.chaty.entity.DocCorrectRecord;
import com.chaty.enums.CorrectEnums.CorrectEventType;
import com.chaty.service.DocCorrectRecordService;
import com.chaty.task.correct.CorrectEvent;
import com.chaty.task.correct.RecordCorrector;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 试卷批改任务
 * 
 * @deprecated 通过消息队列发布任务
 */
@Slf4j
// @Component
public class DocCorrectTask {

    /**
     * 队列大小
     */
    @Value("${doc-correct.queue-size:100}")
    public Integer queueSize;

    @Resource
    private DocCorrectRecordService docCorrectRecordService;
    @Resource
    private RecordCorrector recordCorrector;
    @Resource
    private ThreadPoolTaskExecutor docCorrectTaskExecutor;

    @PostConstruct
    public void init() {
        docCorrectRecordService.reset2Wait();
        execute();
    }

    public void execute() {
        execute(CorrectEvent.type(CorrectEventType.CORRECT));
    }

    @Async
    @EventListener
    @IgnoreTenant
    public synchronized void execute(CorrectEvent e) {
        ThreadUtil.sleep(3000);  // 等待数据库更新;垃圾数据库
        int limit = queueSize - docCorrectTaskExecutor.getQueueSize();
        log.info("DocCorrectTask execute, type: {}, limit: {}", e.getType(), limit);
        // 查询待处理的任务
        List<DocCorrectRecord> records = docCorrectRecordService.listWait(limit);
        for (DocCorrectRecord record : records) {
            // 处理任务
            docCorrectTaskExecutor.execute(() -> recordCorrector.correct(record));
        }
    }

    @Scheduled(initialDelay = 10 * 1000, fixedRate = 5 * 60 * 1000)
    public void taskStatus() {
        int active = docCorrectTaskExecutor.getQueueSize();
        int remain = queueSize - active;
        log.info("DocCorrectTask taskStatus, remain: {}, active: {}", remain, active);
        if (remain > 0) {
            execute();
        }
    }

}
