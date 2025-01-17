package com.chaty.entity.admin;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin_user")
public class AdminUser {

    @TableId
    private String id;

    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
