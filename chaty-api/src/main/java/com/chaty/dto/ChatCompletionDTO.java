package com.chaty.dto;

import java.util.List;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class ChatCompletionDTO {

    private String model;

    private List<MessageDTO> messages;

    private List<FunctionDTO> functions;

    private JSONObject responseFormat;

    private Float temperature;

    private Float topp;

    private Float topk;

    private Float penaltyScore;

    private Boolean stream = false;

    private String userid;

    private Integer maxTokens;

    private Boolean enableSearch;

}
