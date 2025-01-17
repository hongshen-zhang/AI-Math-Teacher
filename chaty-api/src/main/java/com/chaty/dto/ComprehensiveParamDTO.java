package com.chaty.dto;

import cn.hutool.json.JSONObject;
import com.chaty.common.BasePage;
import lombok.Data;

import java.util.List;

@Data
public class ComprehensiveParamDTO extends ComprehensiveResultDTO{
    private BasePage page;

    private List<JSONObject> pages;
}
