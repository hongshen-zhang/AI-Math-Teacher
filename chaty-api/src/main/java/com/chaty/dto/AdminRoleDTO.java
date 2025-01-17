package com.chaty.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminRoleDTO extends BaseDTO {

    private String id;

    private String name;
    private String roleAuth;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
}
