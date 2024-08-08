package com.chaty.service;

import java.util.Collection;
import java.util.Map;

import com.chaty.dto.ChatCompletionDTO;

public interface BasicAiService {

    Map<String, Object> chatForCompletion(ChatCompletionDTO param);

    Boolean isSupport(String model);

    public static BasicAiService findSupport(Collection<BasicAiService> services, String model) {
        for (BasicAiService service : services) {
            if (service.isSupport(model)) {
                return service;
            }
        }
        return null;
    }

}
