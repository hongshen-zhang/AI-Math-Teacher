package com.chaty.task.correct;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.chaty.dto.DocCorrectConfigDTO;
import com.chaty.dto.DocCorrectRecordDTO;

import cn.hutool.json.JSONArray;

@Component
public class CorrectCacheService {

    private final String CACHE_KEY = "CORRECT_RECORD:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public String getRecordKey(String id) {
        return CACHE_KEY + id;
    }

    public void onCorrectRecord(DocCorrectRecordDTO record) {
        String cacheKey = getRecordKey(record.getId());
        DocCorrectConfigDTO config = record.getConfig();
        JSONArray areasObj = config.getAreasObj();
        // 记录缓存
        redisTemplate.opsForHash().put(cacheKey, "areaNum", areasObj.size());
        redisTemplate.opsForHash().put(cacheKey, "areaCorrected", 0);
    }

    public void onAreaCorrected(DocCorrectRecordDTO record) {
        String cacheKey = getRecordKey(record.getId());
        redisTemplate.opsForHash().increment(cacheKey, "areaCorrected", 1);
    }

    public void onRecordCorrected(DocCorrectRecordDTO record) {
        String cacheKey = getRecordKey(record.getId());
        redisTemplate.delete(cacheKey);
    }

    public Map<String, Object> getRecordCache(String id) {
        String cacheKey = getRecordKey(id);
        return (Map) redisTemplate.opsForHash().entries(cacheKey);
    }

}
