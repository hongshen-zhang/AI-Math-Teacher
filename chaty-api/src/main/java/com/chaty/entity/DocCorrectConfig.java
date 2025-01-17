package com.chaty.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("doc_correct_config")
public class DocCorrectConfig {

    @TableId
    private String id;

    private String name;

    private String docurl;

    private String img;

    private String docType;

    private String config;

    private String areas;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private String tenantId;
}