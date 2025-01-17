package com.chaty.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SchoolDTO extends BaseDTO {

    private String schoolId;
    private String schoolName;
    private String address;
    private Integer establishedYear;
    private String phoneNumber;
    private String email;
    private String website;
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
