package com.chaty.cache;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;

@Component
public class DocReviewCacheService {

    @Value("${docreview.cache.size:1000}")
    private Integer docReviewCacheSize;

    private Cache<String, DocReviewCache> docReviewCache;

    @PostConstruct
    public void init() {
        docReviewCache = CacheUtil.newFIFOCache(docReviewCacheSize);
    }

    public void put(String key, DocReviewCache value) {
        docReviewCache.put(key, value);
    }

    public void nextQuestion(String key) {
        DocReviewCache cache = docReviewCache.get(key);
        if (cache != null) {
            cache.setReviewedNum(cache.getReviewedNum() + 1);
            docReviewCache.put(key, cache);
        }
    }

    public DocReviewCache get(String key) {
        return docReviewCache.get(key);
    }

    public void remove(String key) {
        docReviewCache.remove(key);
    }

}
