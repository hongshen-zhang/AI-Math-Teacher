package com.chaty.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Data;

@Data
public class Org {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String orgName;

    private String apiKey;

    private LocalDateTime createTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

}
