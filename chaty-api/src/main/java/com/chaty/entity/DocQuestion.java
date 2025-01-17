package com.chaty.entity;

import lombok.Data;

@Data
public class DocQuestion {

    private Integer id;

    private String question;

    private String correctAnswer;

    private Integer height;

    private Integer deleted;

    private String query;

}
