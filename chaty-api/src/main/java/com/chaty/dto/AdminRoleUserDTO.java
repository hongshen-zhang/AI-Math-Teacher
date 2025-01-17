package com.chaty.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminRoleUserDTO extends BaseDTO {
    
    private String id;

    private String roleId;
    private String userId;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String username;
    private String rolename;
    private String roleAuth;

}
