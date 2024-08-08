package com.chaty.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chaty.api.openai.Message;
import com.chaty.api.openai.OpenaiApi;
import com.chaty.dto.ChatCompletionDTO;
import com.chaty.exception.BaseException;
import com.chaty.service.BasicAiService;
import com.chaty.service.OpenaiService;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;

@Service
public class OpenaiServiceImpl implements OpenaiService, BasicAiService {

    @Resource
    private OpenaiApi openaiApi;

    private Map<String, Object> models = MapUtil
            .builder("gpt-4", null)
            .put("gpt-3.5-turbo", null)
            .build();

    @Override
    public List<Message> completionForMessage(String model, List<Message> messages, float temperature) {
        Map<String, Object> params = new HashMap<>();
        params.put("model", model);
        params.put("temperature", temperature);
        params.put("messages", messages);

        Map<String, Object> res = openaiApi.chatCompletionsV1(params);
        return JSONUtil.parseObj(res).getJSONArray("choices").stream()
                .map(item -> {
                    return JSONUtil.parseObj(item).getBean("message", Message.class);
                }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> chatForCompletion(ChatCompletionDTO param) {
        Map<String, Object> apiParam = new HashMap<>();
        apiParam.put("model", param.getModel());
        apiParam.put("messages", param.getMessages());
        if (Objects.nonNull(param.getTemperature())) {
            apiParam.put("temperature", param.getTemperature());
        }
        if (Objects.nonNull(param.getTopp())) {
            apiParam.put("top_p", param.getTopp());
        }

        Map<String, Object> resp = openaiApi.chatCompletionsV1(apiParam);

        // 异常处理

        resp.put("$response", JSONUtil.getByPath(JSONUtil.parseObj(resp), "choices[0].message.content"));

        return resp;
    }

    @Override
    public Boolean isSupport(String model) {
        return models.containsKey(model);
    }

}
