package com.chaty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.chaty.entity.Review;

@Mapper
public interface ReviewMapper {
    
    List<Review> list(@Param("param") Review param, @Param("keyword") String keyword);

    @Insert("INSERT INTO review (id, name, question, correct_answer, knowledge, answer, is_true, err_text, comment, ai_content) VALUES (#{entity.id}, #{entity.name}, #{entity.question}, #{entity.correctAnswer}, #{entity.knowledge}, #{entity.answer}, #{entity.isTrue}, #{entity.errText}, #{entity.comment}, #{entity.aiContent})")
    void insertOne(@Param("entity") Review entity);

    @Update("UPDATE review SET name = #{entity.name}, question = #{entity.question}, correct_answer = #{entity.correctAnswer}, knowledge = #{entity.knowledge}, answer = #{entity.answer}, is_true = #{entity.isTrue}, err_text = #{entity.errText}, comment = #{entity.comment}, ai_content = #{entity.aiContent} WHERE id = #{entity.id}")
    void updateById(@Param("entity") Review entity);

    @Update("update review set deleted = 1 where id = #{id}")
    void deleteById(@Param("id") String id);

}
