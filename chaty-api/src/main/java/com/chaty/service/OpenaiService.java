package com.chaty.service;

import java.util.List;

import com.chaty.api.openai.Message;

public interface OpenaiService {

    List<Message> completionForMessage(String model, List<Message> messages, float temperature);

}
