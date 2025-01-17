package com.chaty.dto;

import com.chaty.entity.User;

import lombok.Data;

@Data
public class UserDTO extends User {

    private PageDTO<?> page;

    private Integer count;

    private String schoolId;
    
}
