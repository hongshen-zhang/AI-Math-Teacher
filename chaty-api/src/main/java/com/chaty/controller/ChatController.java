package com.chaty.controller;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.dto.ChatCompletionDTO;
import com.chaty.service.BasicAiService;

import reactor.core.publisher.Flux;

@RequestMapping("/api/chat")
@RestController
public class ChatController {

    @Resource
    private Set<BasicAiService> basicAiServices;

    /**
     * $message: 生成的文本
     */
    @PostMapping("/completion")
    public BaseResponse<Map<String, Object>> chatForCompletion(@RequestBody ChatCompletionDTO param) {
        Optional.ofNullable(param.getModel()).orElseThrow(() -> new RuntimeException("模型不能为空"));
        Map<String, Object> res = BasicAiService.findSupport(basicAiServices, param.getModel())
                .chatForCompletion(param);
        return BaseResponse.ok(res);
    }

    @PostMapping(value = "/completion/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCompletetion(@RequestBody ChatCompletionDTO param) {
        return BasicAiService.findSupport(basicAiServices, param.getModel()).streamCompletetion(param);
    }

}
