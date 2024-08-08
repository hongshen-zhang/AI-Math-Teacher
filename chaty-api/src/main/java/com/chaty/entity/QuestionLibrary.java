package com.chaty.entity;

import lombok.Data;

@Data
public class QuestionLibrary {

    private String id;

    private String question;

    private String answer;

    private String knowledge;

    private String relations;

    private Integer deleted;

}
