package com.chaty.dto;

import java.math.BigDecimal;
import java.util.List;

import com.chaty.entity.DocCorrectResult;

import lombok.Data;

@Data
public class DocCorrectResultDTO extends DocCorrectResult {

    private PageDTO<?> page;

    private BigDecimal totalScore;

    private BigDecimal correctNum;
    
    private BigDecimal avgScore;

    private BigDecimal correctRate;

    private String identify;

    private List<String> tagIds;

    private Integer correctRateOrder = 0;

}
