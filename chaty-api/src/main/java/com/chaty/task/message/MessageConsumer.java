package com.chaty.task.message;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.chaty.entity.DocCorrectRecord;
import com.chaty.task.correct.RecordCorrector;
import com.chaty.util.FileUtil;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageConsumer {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private TaskExecutor docCorrectTaskExecutor;
    @Resource
    private RecordCorrector recordCorrector;

    /**
     * 任务数量
     */
    @Value("${doc-correct.task-num:5}")
    public Integer correctRecordTaskNum;

    @PostConstruct
    public void init() {
        launchCorrectRecordTask();
    }

    public void launchCorrectRecordTask() {
        log.info("launchCorrectRecordTask: taskNum->{}", correctRecordTaskNum);
        for (int i = 0; i < correctRecordTaskNum; i++) {
            docCorrectTaskExecutor.execute(() -> {
                while (true) {
                    try {
                        CorrectRecordMessage message = (CorrectRecordMessage) redisTemplate.opsForList()
                                .rightPop(MessageConsts.MESSAGE_QUEUE, 10, TimeUnit.SECONDS);
                        if (Objects.nonNull(message)) {
                            doCorrectRecord(message);
                        }
                    } catch (Exception e) {
                        log.error("doCorrectRecord error: {}", e.getMessage());
                        ThreadUtil.sleep(3000);
                    }
                }
            });
        }
    }

    public void doCorrectRecord(CorrectRecordMessage message) {
        DocCorrectRecord record = message.getRecord();
        log.info("doCorrectRecord: {}", record.getId());
        // 预加载文件
        FileUtil.INSTANCE.preLoadFile(record.getDocurl());
        // 试卷批改
        recordCorrector.correct(record);
    }

}
