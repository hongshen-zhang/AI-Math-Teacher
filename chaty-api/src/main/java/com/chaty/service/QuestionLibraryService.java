package com.chaty.service;

import java.util.List;
import java.util.Map;

import com.chaty.entity.QuestionLibrary;

public interface QuestionLibraryService {

    List<QuestionLibrary> findAll(QuestionLibrary param);

    void add(QuestionLibrary param);

    void update(QuestionLibrary param);

    void delete(String id);

    List<Map<String, ?>> findByKeyword(String keyword, Integer searchType);

}
