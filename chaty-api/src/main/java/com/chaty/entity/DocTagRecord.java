package com.chaty.entity;

import lombok.Data;

@Data
public class DocTagRecord {
    
    private String id;

    private String tagId;

    private String docId;

    private String taskId;

    private Integer deleted;

}
