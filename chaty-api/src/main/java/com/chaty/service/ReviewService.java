package com.chaty.service;

import java.util.List;

import com.chaty.entity.Review;

public interface ReviewService {

    List<Review> list(Review param, String keyword);

    void insertOne(Review entity);

    void updateById(Review entity);

    void deleteById(String id);

}
