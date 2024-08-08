package com.chaty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.chaty.entity.Knowledge;

@Mapper
public interface KnowledgeMapper {

    List<Knowledge> list(@Param("param") Knowledge param, @Param("keyword") String keyword);

    @Insert("insert into knowledge (id, name, content) values (#{entity.id}, #{entity.name}, #{entity.content})")
    void insertOne(@Param("entity") Knowledge entity);

    @Update("update knowledge set name=#{entity.name}, content=#{entity.content} where id = #{entity.id}")
    void updateById(@Param("entity") Knowledge entity);

    @Update("update knowledge set deleted = 1 where id = #{id}")
    void deleteById(@Param("id") String id);

}
