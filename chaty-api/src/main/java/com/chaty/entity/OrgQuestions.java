package com.chaty.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Data;

@Data
public class OrgQuestions {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String question;

    private String correctAnswer;

    private BigDecimal score;

    private String scoringMode;

    private String questionType;

    private String questionInfo;

    private String qsNo;

    private String orgId;

    private LocalDateTime createTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

}
