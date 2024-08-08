package com.chaty.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.chaty.entity.QuestionLibrary;

@Mapper
public interface QuestionLibraryMapper {

    List<QuestionLibrary> selectAll(@Param("param") QuestionLibrary param);

    @Update("insert into question_library (id, question, answer, knowledge) values (#{param.id}, #{param.question}, #{param.answer}, #{param.knowledge})")
    void insert(@Param("param") QuestionLibrary param);

    @Update("update question_library set question = #{param.question}, answer = #{param.answer}, knowledge = #{param.knowledge} where id = #{param.id}")
    void updateById(@Param("param") QuestionLibrary param);

    @Update("update question_library set deleted = 1 where id = #{id}")
    void delete(@Param("id") String id);

    List<Map<String, ?>> findByKeyword(@Param("keyword") String keyword, @Param("searchType") Integer searchType);
}
