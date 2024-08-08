package com.chaty.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.api.openai.Message;
import com.chaty.common.BaseResponse;
import com.chaty.service.OpenaiService;

@RequestMapping("/api/openai")
@RestController
public class OpenaiController {

    @Resource
    private OpenaiService openaiService;

    @PostMapping("/completionForMessage")
    public BaseResponse<List<Message>> completionForMessage(@RequestParam(defaultValue = "gpt-3.5-turbo") String model,
            @RequestParam(defaultValue = "1") float temperature,
            @RequestBody List<Message> messages) {
        List<Message> res = openaiService.completionForMessage(model, messages, temperature);
        return BaseResponse.ok(res);
    }

}
