package com.chaty.entity.admin;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("admin_role_user")
public class AdminRoleUser {

    @TableId
    private String id;

    private String roleId;
    private String userId;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
    
}
