package com.chaty.dto;

import lombok.Data;

@Data
public class SchoolUserDTO extends BaseDTO {

    private String id;

    private String schoolId;

    private String userId;

    private String createTime;

    private String updateTime;

    private String username;

    private String schoolName;

}
