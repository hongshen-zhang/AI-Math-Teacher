package com.chaty.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ComprehensiveResultDTO {
    private String id;
    private String name;
    private String url;
    private Integer status;
    private Integer isEssay;
    private String creator;
    private String config;
    private Date createTime;
    private Date updateTime;
    private Integer type;
}

