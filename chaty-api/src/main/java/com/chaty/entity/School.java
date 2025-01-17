package com.chaty.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("school")
public class School {

    @TableId(type = IdType.ASSIGN_UUID)
    private String schoolId;

    @TableField("school_name")
    private String schoolName;

    private String address;
    private Integer establishedYear;
    private String phoneNumber;
    private String email;
    private String website;
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;

}
