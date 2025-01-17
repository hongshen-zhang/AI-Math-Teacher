package com.chaty.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("doc_correct_result")
public class DocCorrectResult {

    private String id;

    private String configId;

    private String configName;

    private Integer areaIdx;

    private Integer qsIdx;

    private String taskId;

    private String taskName;

    private String recordId;

    private String recordName;

    private String areaUrl;

    private String question;

    private String answer;

    private String stuAnswer;

    private Integer isCorrect;

    private BigDecimal score;

    private BigDecimal scored;

    private String review;

    private String qsInfo;

    private BigDecimal correctRate;

    @TableLogic
    private Integer deleted;

}