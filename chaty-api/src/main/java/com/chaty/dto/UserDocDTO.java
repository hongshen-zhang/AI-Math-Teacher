package com.chaty.dto;

import java.util.List;

import com.chaty.entity.UserDoc;

import lombok.Data;

@Data
public class UserDocDTO extends UserDoc {
    
    private List<UserDoc> userDocs;

    private List<String> docIds;

    private List<String> taskIds;

}
