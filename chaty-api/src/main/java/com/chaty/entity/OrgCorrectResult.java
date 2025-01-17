package com.chaty.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Data;

@Data
public class OrgCorrectResult {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String qsId;

    private String qsNo;

    private String orgId;

    private String correctImage;

    private String correctAnswer;

    private boolean isCorrect;

    private String studentAnswer;

    private BigDecimal score;

    private String comment;

    private Integer points;

    private LocalDateTime createTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

}
