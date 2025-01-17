package com.chaty.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminUserDTO extends BaseDTO {

    private String id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String accessToken;

}
