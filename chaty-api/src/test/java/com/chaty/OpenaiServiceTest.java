package com.chaty;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.chaty.dto.ChatCompletionDTO;
import com.chaty.dto.MessageDTO;
import com.chaty.service.BasicAiService;
import com.chaty.service.OpenaiService;

@SpringBootTest(classes = ChatyApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OpenaiServiceTest {

    @Resource
    public BasicAiService openaiServiceImpl;

    @Test
    public void test() {
        ChatCompletionDTO param = new ChatCompletionDTO();
        param.setModel("gpt-3.5-turbo");
        MessageDTO message = new MessageDTO();
        message.setRole("user");
        message.setContent("hello, 介绍一下你自己");
        param.setMessages(Collections.singletonList(message));
        openaiServiceImpl.streamCompletetion(param).subscribe(System.out::println);
    }

}
