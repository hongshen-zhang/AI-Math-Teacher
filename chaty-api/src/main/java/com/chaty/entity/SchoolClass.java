package com.chaty.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("class")
public class SchoolClass {

    @TableId(type = IdType.ASSIGN_UUID)
    @TableField("class_id")
    private String classId;

    @TableField("school_id")
    private Integer schoolId;

    @TableField("class_name")
    private String className;

    @TableField("teacher_user_id")
    private Integer teacherUserId;

    @TableField("grade_name")
    private String gradeName;
}
