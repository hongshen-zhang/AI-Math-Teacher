package com.chaty.dto;

import java.util.List;

import lombok.Data;

@Data
public class ChatCompletionDTO {

    private String model;

    private List<MessageDTO> messages;

    private Float temperature;

    private Float topp;

    private Float penaltyScore;

    private Boolean stream = false;

    private String userid;

}
