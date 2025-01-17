package com.chaty.dto;

import java.util.List;

import com.chaty.common.BasePage;
import com.chaty.entity.DocCorrectFile;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class DocCorrectFileDTO extends DocCorrectFile {

    private String code;

    private BasePage page;

    private List<JSONObject> pages;

    /**
     * 操作 submit-提交 correct-提交并批改
     */
    private String action;

    /**
     * 排序字段
     */
    private String orderBy;
}
