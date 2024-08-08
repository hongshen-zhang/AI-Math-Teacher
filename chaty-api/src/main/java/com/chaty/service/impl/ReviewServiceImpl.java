package com.chaty.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chaty.entity.Review;
import com.chaty.mapper.ReviewMapper;
import com.chaty.service.ReviewService;

import cn.hutool.core.util.IdUtil;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Resource
    private ReviewMapper reviewMapper;

    @Override
    public void deleteById(String id) {
        reviewMapper.deleteById(id);
    }

    @Override
    public void insertOne(Review entity) {
        entity.setId(IdUtil.fastSimpleUUID());
        reviewMapper.insertOne(entity);
    }

    @Override
    public List<Review> list(Review param, String keyword) {
        return reviewMapper.list(param, keyword);
    }

    @Override
    public void updateById(Review entity) {
        reviewMapper.updateById(entity);
    }

}
