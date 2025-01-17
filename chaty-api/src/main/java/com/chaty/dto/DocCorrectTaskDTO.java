package com.chaty.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.chaty.entity.DocCorrectTask;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class DocCorrectTaskDTO extends DocCorrectTask {

    private PageDTO<DocCorrectTask> page;

    private String configName;

    private String aimodel;

    private String ocrType;

    private Boolean responseFormat;

    JSONObject correctConfigObj;

    private String filename;

    private List<List<JSONObject>> docList;

    private Boolean scoreMerge;

    private Boolean isPreview;

    private List<String> taskIds;

    private Integer count;

    private List<String> recordIds;

    private boolean countRecord;

    private Integer recordCount;

    private Integer finishedCount;

    private Integer fontSize;

    private BigDecimal segedScore;

    private Boolean showQsScore = false;

    private String[] tagIds;

    private String taskId;

    private Boolean orderByName = false;

    private Boolean isCreateConfig;

    private Boolean isReversed = false;

    private Boolean isRotate = false;
    
    private String similarPaperId;

    private SaveRemoteFileDTO remoteFileProps;

    private PrinterPropsDTO printerProps;

    private String name;

    public String getCorrectConfigStr() {
        return JSONUtil.createObj()
                .set("aimodel", aimodel)
                .set("ocrType", ocrType)
                .set("responseFormat", responseFormat)
                .toString();
    }

    public JSONObject getCorrectConfigObj() {
        return Optional.ofNullable(correctConfigObj)
                .orElseGet(() -> JSONUtil.parseObj(getCorrectConfig()));
    }

    public String getAimodel() {
        return Optional.ofNullable(aimodel)
                .orElseGet(() -> getCorrectConfigObj().getStr("aimodel"));
    }

    public String getOcrType() {
        return Optional.ofNullable(ocrType)
                .orElseGet(() -> getCorrectConfigObj().getStr("ocrType"));
    }

    public Boolean getResponseFormat() {
        return Optional.ofNullable(responseFormat)
                .orElseGet(() -> getCorrectConfigObj().getBool("responseFormat", false));
    }

}
