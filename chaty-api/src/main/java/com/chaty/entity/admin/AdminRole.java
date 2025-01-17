package com.chaty.entity.admin;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("admin_role")
public class AdminRole {

    @TableId
    private String id;

    private String name;
    private String roleAuth;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;

}
