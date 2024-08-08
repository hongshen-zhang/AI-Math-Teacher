package com.chaty.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chaty.entity.QuestionLibrary;
import com.chaty.mapper.QuestionLibraryMapper;
import com.chaty.service.QuestionLibraryService;

import cn.hutool.core.util.IdUtil;

@Service
public class QuestionLibraryServiceImpl implements QuestionLibraryService {

    @Resource
    private QuestionLibraryMapper questionLibraryMapper;

    @Override
    public List<QuestionLibrary> findAll(QuestionLibrary param) {
        return questionLibraryMapper.selectAll(param);
    }

    @Override
    public void add(QuestionLibrary param) {
        param.setId(IdUtil.fastSimpleUUID());
        questionLibraryMapper.insert(param);
    }

    @Override
    public void delete(String id) {
        questionLibraryMapper.delete(id);
    }

    @Override
    public void update(QuestionLibrary param) {
        questionLibraryMapper.updateById(param);
    }

    @Override
    public List<Map<String, ?>> findByKeyword(String keyword, Integer searchType) {
        return questionLibraryMapper.findByKeyword(keyword, searchType);
    }

}
