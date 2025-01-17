package com.chaty.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("school_user")
public class SchoolUser {

    @TableId
    private String id;

    private String schoolId;

    private String userId;

    private String createTime;

    private String updateTime;

    private Integer deleted;

}
