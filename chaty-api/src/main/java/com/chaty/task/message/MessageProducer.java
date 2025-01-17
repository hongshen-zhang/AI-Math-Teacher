package com.chaty.task.message;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.chaty.entity.DocCorrectRecord;
import com.chaty.service.DocCorrectRecordService;
import com.chaty.task.correct.CorrectEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageProducer {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private DocCorrectRecordService docCorrectRecordService;

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void onRecordCorrect(CorrectEvent e) {
        log.info("Handle correct record event: {}", e);
        // 查询所有待批改任务
        List<DocCorrectRecord> records = docCorrectRecordService.listWait(null);
        List<Object> messageList = records.stream().map(record -> {
            return new CorrectRecordMessage(record);
        }).collect(Collectors.toList());
        // 发送消息
        redisTemplate.opsForList().leftPushAll(MessageConsts.MESSAGE_QUEUE, messageList);
        log.info("Send correct record message size: {}", messageList.size());
    }

}
