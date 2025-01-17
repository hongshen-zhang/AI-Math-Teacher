package com.chaty.entity;

import java.util.Date;
import java.util.List;

import com.chaty.common.BasePage;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class DocReviewRec extends BasePage {
    
    private String id;

    private String docReviewId;

    private String libraryId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Integer deleted;


    /**
     * reviewIds
     */
    private List<String> reviewIds;
    private String startTime;
    private String endTime;
    private String datePrefix;

    private String docurl;
    private String docname;
    private String docpath;
    private String questions;
    private String filename;
    private String fileurl;
    private String identify;
    private Integer status;
    private String errorText;
    private String reviewDoc;
    private String reviewed; 

}
