package com.chaty.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("doc_correct_file")
public class DocCorrectFile {
    @TableId(type = IdType.AUTO)
    private String id;

    private String name;

    private String url;

    private Integer status;

    private Integer deleted;

    private Date createTime;    

    private String creator;

    private String modelValue;

    private String tenantId;

    private Integer isEssay;
}
