package com.chaty.entity;

import java.sql.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@TableName("doc_correct_record")
@Data
public class DocCorrectRecord {

    @TableId
    private String id;

    private String taskId;

    private String configId;

    private String docurl;

    private String docname;

    private String identify;

    private Integer status;

    private String error;

    private String reviewed;

    private Integer hasChange;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableLogic
    private Integer deleted;

    private String tenantId;
}
