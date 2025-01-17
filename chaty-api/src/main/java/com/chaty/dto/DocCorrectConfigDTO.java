package com.chaty.dto;

import java.util.Optional;

import com.chaty.entity.DocCorrectConfig;
import com.chaty.util.FileUtil;
import com.chaty.util.PDFUtil;
import com.tencentcloudapi.bma.v20210624.models.File;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class DocCorrectConfigDTO extends DocCorrectConfig {

    private PageDTO<DocCorrectConfig> page;

    private JSONObject configObj;

    private JSONArray areasObj;

    public JSONObject getConfigObj() {
        return Optional.ofNullable(configObj)
                .orElseGet(() -> JSONUtil.parseObj(getConfig()));
    }

    public JSONArray getAreasObj() {
        return Optional.ofNullable(areasObj)
                .orElseGet(() -> JSONUtil.parseArray(getAreas()));
    }

    public JSONObject getDocInfo() {
        return PDFUtil.getPDFInfo(FileUtil.INSTANCE.relUrl2Path(getDocurl()));
    }

}
