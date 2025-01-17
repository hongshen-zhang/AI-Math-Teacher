package com.chaty.entity;

import lombok.Data;

@Data
public class DocLibrary {

    private String id;

    private String docurl;

    private String docpath;

    private String docname;

    private String idArea;

    private String questions;

    private Integer deleted;

    private String extConf;
}
