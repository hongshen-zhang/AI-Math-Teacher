package com.chaty.entity;

import java.util.List;

import lombok.Data;

@Data
public class DocReviewed {

    private Integer id;

    private String filename;

    private String fileurl;

    private String reviewed;

    private String reviewedDoc;

    private Integer status;

    private String error;

    private String batchId;

    private List<Integer> ids;

}
