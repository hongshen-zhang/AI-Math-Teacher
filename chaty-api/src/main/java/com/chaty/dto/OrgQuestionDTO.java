package com.chaty.dto;

import com.chaty.entity.OrgQuestions;

import lombok.Data;

@Data
public class OrgQuestionDTO extends OrgQuestions {

    private String correctImage;

    private String correctAnswer;

}
