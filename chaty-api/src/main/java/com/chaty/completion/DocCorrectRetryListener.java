package com.chaty.completion;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DocCorrectRetryListener implements RetryListener {

    private ConcurrentHashMap<Integer, Integer> retryCountMap = new ConcurrentHashMap<>();
    private AtomicInteger correctCount = new AtomicInteger(0);

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
            Throwable throwable) {
        log.info("doc correct retry close: retrycount={}", context.getRetryCount());
        retryCountMap.compute(context.getRetryCount(), (k, v) -> v == null ? 1 : v + 1);            
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
            Throwable throwable) {
    }

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        int count = correctCount.incrementAndGet();
        log.info("doc correct retry open: count={}", count);
        return true;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void printRetryStatistic() {
        log.info("doc correct retry statistic: \ncount=>{} \n{}", correctCount.get(), JSONUtil.toJsonPrettyStr(retryCountMap));
    }
    
}
