package com.chaty.dto;

import java.util.List;

import com.chaty.entity.DocTag;

import lombok.Data;

@Data
public class DocTagDTO extends DocTag {
    
    private List<String> docIds;

    private List<String> taskIds;

    private String docId;

    private String taskId;

    private List<String> ids;

}
