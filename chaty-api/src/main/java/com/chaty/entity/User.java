package com.chaty.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user")
@Data
public class User {

    private String id;

    private String username;

    private String email;

    private String nickname;

    private String password;

    private Integer status;

    private Integer role;

    private Integer deleted;

    @TableField("class_id")
    private String classId;

    // 学号
    @TableField("student_id")
    private String studentId;

    /**
     * 默认学校
     */
    private String defaultSchool;
}
