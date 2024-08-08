package com.chaty.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chaty.entity.Knowledge;
import com.chaty.mapper.KnowledgeMapper;
import com.chaty.service.KnowledgeService;

import cn.hutool.core.util.IdUtil;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    @Resource
    private KnowledgeMapper knowledgeMapper;

    @Override
    public void add(Knowledge entity) {
        entity.setId(IdUtil.fastSimpleUUID());
        knowledgeMapper.insertOne(entity);
    }

    @Override
    public void deleteById(String id) {
        knowledgeMapper.deleteById(id);
    }

    @Override
    public List<Knowledge> list(Knowledge param, String keyword) {
        return knowledgeMapper.list(param, keyword);
    }

    @Override
    public void updateById(Knowledge entity) {
        knowledgeMapper.updateById(entity);
    }

}
