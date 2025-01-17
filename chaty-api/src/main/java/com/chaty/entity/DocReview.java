package com.chaty.entity;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class DocReview {

    private String id;

    private String filename;

    private String fileurl;

    private String identify;

    private Integer status;

    private String errorText;

    private String reviewDoc;

    private Integer deleted;

    private String batchId;

    private String reviewed;

    /**
     * 查询参数
     */
    private List<String> incluedIds; // ID集合
    /**
     * 设置缓存
     */
    private Boolean setProgress = false;
    /**
     * 批改进度
     */
    private Object progress;

}
