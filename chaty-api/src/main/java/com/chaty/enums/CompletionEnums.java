package com.chaty.enums;

import java.awt.Image;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.chaty.dto.ChatCompletionDTO;
import com.chaty.dto.FunctionDTO;
import com.chaty.dto.FunctionDTO.Parameters;
import com.chaty.dto.FunctionDTO.Parameters.Property;
import com.chaty.dto.MessageDTO;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.Getter;

public enum CompletionEnums {

    AI_REVIEW(docReciewCompletion(), "check_answer"),
    AI_OCR(ocrCompletion(), ""),
    VISION_DOVREVIEW(visionDocReview(), ""),
    COMPLETION_DOCREVIEW(completionDocReview(), "");

    private ChatCompletionDTO completion;
    @Getter
    private String funcName;

    private CompletionEnums(ChatCompletionDTO completion, String funcName) {
        this.completion = completion;
        this.funcName = funcName;
    }

    public ChatCompletionDTO getCompletion(String model, String... params) {
        ChatCompletionDTO c = JSONUtil.toBean(JSONUtil.toJsonStr(completion), ChatCompletionDTO.class, false);
        c.setModel(model);
        MessageDTO message = c.getMessages().get(0);
        message.setContent(String.format((String) message.getContent(), params));
        return c;
    }

    public ChatCompletionDTO getVisionCompletion(String model, String url, String... params) {
        ChatCompletionDTO c = JSONUtil.toBean(JSONUtil.toJsonStr(completion), ChatCompletionDTO.class, false);
        c.setModel(model);
        MessageDTO message = c.getMessages().get(0);
        JSONArray contentTemplate = JSONUtil.parseArray((String) message.getContent());
        contentTemplate.putByPath("[0].text", 
            String.format((String)contentTemplate.getByPath("[0].text"), params));
        contentTemplate.putByPath("[1].image_url.url", url);

        // claude 模型消息格式处理
        if (AIModelConsts.claudeModels.contains(model)) {
            gptV2ClaudeV(contentTemplate);
        }

        message.setContent(contentTemplate);
        return c;
    }

    private void gptV2ClaudeV(JSONArray content) {
        String url = content.getByPath("[1].image_url.url", String.class);
        Image image = ImgUtil.getImage(URLUtil.url(url));
        String base64 = ImgUtil.toBase64(image, ImgUtil.IMAGE_TYPE_JPG);
        String imgMsgTemplate = "{\"type\":\"image\",\"source\":{\"type\":\"base64\",\"media_type\":\"image/jpeg\",\"data\":\"%s\"}}";
        String imgMsg = String.format(imgMsgTemplate, base64);
        content.set(1, JSONUtil.parseObj(imgMsg));
    }

    private static ChatCompletionDTO docReciewCompletion() {
        ChatCompletionDTO completion = new ChatCompletionDTO();

        // 消息
        MessageDTO message = new MessageDTO();
        message.setRole("user");
        message.setContent(
                "严格使用中文;严格使用'\\\\\\\\'代替'\\\\';严格在数学公式前后使用'$';严格使用Latex格式;\n"
                        +
                        "题目: %s \n" +
                        "正确答案: %s \n" +
                        "学生答案: %s");
        completion.setMessages(Collections.singletonList(message));

        // 方法
        FunctionDTO function = new FunctionDTO();
        function.setName("check_answer");
        function.setDescription("请扮演老师严格批改作业;严格使用中文;严格使用'\\\\\\\\'代替'\\\\';严格在数学公式前后使用'$';严格使用Latex格式");
        Parameters parameters = new Parameters(); // 参数
        parameters.setType("object");
        Map<String, Property> properties = new HashMap<String, Property>();
        Property isTrueProp = new Property(); // isTrue
        isTrueProp.setType("boolean");
        isTrueProp.setDescription("根据标准答案判断学生答案是否完全正确,如果部分错误视为错误");
        properties.put("isTrue", isTrueProp);
        Property reviewProp = new Property(); // 评价
        reviewProp.setType("string");
        reviewProp.setDescription(
                "如果正确请简单表扬学生，如果错误请直接指出学生答案中的具体错误;严格使用中文;严格使用'\\\\\\\\'代替'\\\\';严格在数学公式前后使用'$';严格使用Latex格式");
        properties.put("review", reviewProp);
        parameters.setProperties(properties);
        function.setParameters(parameters);
        function.setRequired(new String[] { "isTrue", "review" }); // required
        completion.setFunctions(Collections.singletonList(function));

        return completion;
    }

    private static ChatCompletionDTO ocrCompletion() {
        ChatCompletionDTO completion = new ChatCompletionDTO();

        // 消息
        MessageDTO message = new MessageDTO();
        message.setRole("user");
        message.setContent("[\r\n" + //
                "    {\r\n" + //
                "        \"type\":\"text\",\r\n" + //
                "        \"text\":\"你是一个OCR工具，你需要识别图片中的文本。\\r\\n" + //
                "\\r\\n" + //
                " 你需要严格遵守下面的规则：\\r\\n" + //
                " - 只回复图片中的文本内容，不需要提供任何的提示性的信息\\r\\n" + //
                " - 如果图片中没有有效的文本信息，回复无即可\\r\\n" + //
                " - 如果图片中包含数学公式，请识别latex格式的内容\\r\\n" + //
                " - 请牢记你的角色，你只是一个OCR工具，你只会回复图片中的文本内容\"\r\n" + //
                "    },\r\n" + //
                "    {\r\n" + //
                "        \"type\":\"image_url\",\r\n" + //
                "        \"image_url\":{\r\n" + //
                "            \"url\":\"%s\"\r\n" + //
                "        }\r\n" + //
                "    }\r\n" + //
                "]");
        completion.setMessages(Collections.singletonList(message));
        return completion;
    }


    private static ChatCompletionDTO visionDocReview() {
        ChatCompletionDTO completion = new ChatCompletionDTO();
        completion.setMaxTokens(4096); // gpt-4-vision-preview
        completion.setTemperature(0f);
         // 消息
        MessageDTO message = new MessageDTO();
        message.setRole("user");
        // message.setContent("[{\"type\":\"text\",\"text\":\"你是一名阅卷老师，你需要比较问题的学生答案与正确答案是否一致，并结合题目给出你的评价。\\n图片中就是学生在试卷上填写的答案，识别图片中的文本作为学生答案，如果无法识别学生答案则为学生答案为错误。\\n\\n在阅卷过程中，你需要严格遵守下面的规则：\\n- 学生答案和正确答案是否一致，如果一致，则结果为正确，否则为错误。\\n- 只需要对学生答案和正确答案进行比较，不要质疑正确答案的准确性\\n- 在给出你的评价之前，请充分阅读题目，理解学生的答案，不要将自己的主观意见强加给学生，评价应该基于题目和学生答案本身\\n- 严格按照指定的格式输出评价结果，不要有任何多余的输出\\n- 严格使用使用'1'表示学生答案正确，使用'0'表示学生答案不正确\\n\\n下面是输出的格式，其中\\\\\\\"{xxx}\\\\\\\"表示占位符：\\n### 是否正确\\n{是否正确}\\n\\n### 评语\\n{评语}\\n\\n请严格遵守评卷规则，否则会被扣除评卷积分，下面是题目相关信息:\\n### 题目\\n%s\\n\\n### 正确答案\\n%s\"},{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\"}}]");
        /* message.setContent("[{\"type\":\"text\",\"text\":\"角色：阅卷老师\\\\n" + //
                "任务：判断图片中的学生作业的正确性，请严格遵守指令，否则会被扣除积分,\\\\n" + //
                "\\\\n" + //
                "指令：\\\\n" + //
                "1. 比较学生答案与标准答案：如果相同，则判定为正确（使用'1'表示）；否则判定为错误（使用'0'表示）。\\\\n" + //
                "2. 答案不可识别即为错误：忽略黑色边框，若学生答案完全无法识别，则视为错误。\\\\n" + //
                "3. 严格遵循格式：按照指定格式简单输出评价结果，请勿简单描述学生答案，避免任何多余输出。\\\\n" + //
                "\\\\n" + //
                "输出格式：\\\\n" + //
                "### 是否正确\\\\n" + //
                "{是否正确}\\\\n" + //
                "\\\\n" + //
                "### 描述学生答案\\\\n" + //
                "{描述学生答案}\\\\n" + //
                "\\\\n" + //
                "相关信息: \\\\n" + //
                "- 标准答案：%s\"},{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\"}}]"); */
        /* message.setContent("[{\"type\":\"text\",\"text\":\"角色：阅卷老师\\n" + //
                "任务：图片中为残疾学生的手写作业，请原谅他写字方面的不清晰，尽一切可能帮他准确识别，根据标准答案判断图片中的学生作业的正确性，请严格遵守指令，否则会被扣除积分。 \\n" + //
                "\\n" + //
                "- 标准答案：%s \\n" + //
                "\\n" + //
                "- 题目: %s  \\n" + //
                "\\n" + //
                "指令：\\n" + //
                "1. 比较学生最终答案与标准答案：如果相同，则判定为正确（使用'1'表示）；否则判定为错误（使用'0'表示）。\\n" + //
                "2. 答案不可识别即为错误：忽略黑色边框，若学生答案完全无法识别，则视为错误。\\n" + //
                "3. 严格遵循格式：按照指定格式简单输出评价结果，避免任何多余输出。\\n" + //
                "4. 使用Tex格式输出公式。\\n" + //
                "\\n" + //
                "输出格式：\\n" + //
                "### 是否正确\\n" + //
                "{是否正确} \\n\\n" + 
                "### 描述学生答案\\n" + //
                "{描述学生答案}\"},{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\",\"detail\":\"high\"}}]"); */
        message.setContent("[{\"type\":\"text\",\"text\":\"角色：善良的阅卷老师\\n" + //
                        "任务：图片中为失去父母的手臂残疾学生的手写作业，请原谅他写字方面的不清晰，尽一切可能帮他准确识别，根据标准答案判断图片中的学生作业的正确性，请严格遵守指令，否则会被扣除积分。 \\n" + //
                        "\\n" + //
                        "- 标准答案：%s \\n" + //
                        "\\n" + //
                        "- 题目：%s  \\n" + //
                        "\\n" + //
                        "指令：\\n" + //
                        "1. 比较学生最终答案与标准答案：如果相同，则判定为正确（使用'1'表示）；否则判定为错误（使用'0'表示）。\\n" + //
                        "2. 答案不可识别即为错误：忽略黑色边框，若学生答案完全无法识别，则视为错误。\\n" + //
                        "3. 严格遵循格式：按照指定格式简单输出评价结果，避免任何多余输出。\\n" + //
                        "4. 使用Tex格式输出公式。\\n" + //
                        "5. 当标准答案与学生答案数值相同时，忽略计算过程中的错误。 \\n" + //
                        "\\n" + //
                        "输出格式：\\n" + //
                        "### 是否正确\\n" + //
                        "{是否正确} \\n\\n### 描述学生答案\\n" + //
                        "{描述学生答案}\"},{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\",\"detail\":\"high\"}}]");
        completion.setMessages(Collections.singletonList(message));
        return completion;
    }

    private static ChatCompletionDTO completionDocReview() {
        ChatCompletionDTO completion = new ChatCompletionDTO();

        MessageDTO message = new MessageDTO();
        message.setRole("user");
        message.setContent("角色：善良的阅卷老师\\n任务：根据标准答案判断学生作业的正确性，请严格遵守指令，否则会被扣除积分。 \\n\\n- 标准答案：%s \\n\\n- 题目：1+1=%s \\n\\n- 学生答案：%s  \\n\\n指令：\\n1. 比较学生最终答案与标准答案：如果相同，则判定为正确（使用'1'表示）；否则判定为错误（使用'0'表示）。\\n2. 答案不可识别即为错误：忽略黑色边框，若学生答案完全无法识别，则视为错误。\\n3. 严格遵循格式：按照指定格式简单输出评价结果，避免任何多余输出。\\n4. 使用Tex格式输出公式。\\n5. 当标准答案与学生答案数值相同时，忽略计算过程中的错误。 \\n\\n输出格式：\\n### 是否正确\\n{是否正确} \\n\\n### 描述学生答案\\n{描述学生答案}");
        completion.setMessages(Arrays.asList(message));

        return completion;
    }
}
