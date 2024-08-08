package com.chaty.api.openai;

import lombok.Data;

@Data
public class Message {
    private String role;
    private String content;
}