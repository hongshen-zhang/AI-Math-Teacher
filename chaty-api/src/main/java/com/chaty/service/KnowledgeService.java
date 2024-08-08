package com.chaty.service;

import java.util.List;

import com.chaty.entity.Knowledge;

public interface KnowledgeService {

    List<Knowledge> list(Knowledge param, String keyword);

    void add(Knowledge entity);

    void updateById(Knowledge entity);

    void deleteById(String id);

}
