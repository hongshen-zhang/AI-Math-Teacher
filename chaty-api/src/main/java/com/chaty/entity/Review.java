package com.chaty.entity;

import lombok.Data;

@Data
public class Review {

    private String id;

    private String name;

    private String question;

    private String correctAnswer;

    private String knowledge;

    private String answer;

    private Integer isTrue;

    private String errText;

    private String comment;

    private String aiContent;

    private Integer deleted;

}
