package com.chaty.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@TableName("doc_correct_config_package")
@Data
public class DocCorrectConfigPackage {

    @TableId
    private String id;

    private String name;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private String config;

    private String tenantId;
}

