package com.chaty.dto;

import java.util.List;

import com.chaty.entity.DocReviewed;

import lombok.Data;

@Data
public class DocReviewedDTO {

    private String aimodel;

    private String ocrService;

    private List<Integer> ids;

    private String batchId;

    private List<DocReviewed> docs;

    private int signSize = 30;

    private int fontSize = 4;

    private Boolean preview = false;

}
