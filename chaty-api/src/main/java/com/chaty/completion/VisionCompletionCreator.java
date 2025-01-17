package com.chaty.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.chaty.enums.AIModelMaxTokenConsts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chaty.dto.ChatCompletionDTO;
import com.chaty.dto.DocCorrectRecordDTO;
import com.chaty.dto.MessageDTO;
import com.chaty.enums.AIModelConsts;
import com.chaty.enums.PromptsConsts;
import com.chaty.form.ExtraQsForm;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VisionCompletionCreator implements CompletionCreator {

    @Value("${server.url}")
    public String serverUrl;

    @Override
    public ChatCompletionDTO createDocAreaCompletion(DocCorrectRecordDTO record, JSONObject areaObj,
            JSONObject areaRes) {
        String aimodel = record.getAimodel();
        ChatCompletionDTO completion = new ChatCompletionDTO();
        completion.setModel(aimodel);
        completion.setMaxTokens(4096);
        // 系统提示词
        List<MessageDTO> messages = new ArrayList<MessageDTO>();
        String sysMsg = PromptsConsts.DOC_CORRECT_V2;
        if (Objects.nonNull(record.getConfig())) {
            sysMsg = record.getConfig().getConfigObj().getStr("prompt", sysMsg);
        }
        setSystemMessage(messages, aimodel, sysMsg);
        // 批改逻辑
        StringBuilder content = new StringBuilder("# 下面是所有的问题和答案：\n---\n");
        JSONArray questions = areaObj.getJSONArray("questions");
        questions.forEach(qs -> {
            JSONObject question = (JSONObject) qs;
            content.append(String.format(PromptsConsts.DOC_CORRECT_CONTENT_TEMPLATE_V1,
                    question.getStr("question"), question.getStr("qsInfo", ""), 
                    question.getStr("answer"), question.getStr("scorePoints", ""),
                    question.getStr("score", "")));
            content.append("\n---\n");
        });
        log.info("request content: {} {} \n {}", record.getId(), record.getDocname(), content.toString());
        String areaImg = areaRes.getStr("areaImg");
        MessageDTO message;
        if (areaImg.startsWith("data:image")) {
            message = createImgMessage(aimodel, "user", content.toString(), false, areaImg);
        } else {
            String areaImgUrl = areaImg;
            if (!areaImg.startsWith("http")) {
                areaImgUrl = String.format("%s%s", serverUrl, areaImg);
            }
            message = createImgMessage(aimodel, "user", content.toString(), true, areaImgUrl);
        }
        messages.add(message);

        completion.setMessages(messages);
        return completion;
    }

    @Override
    public boolean isSupported(String aimodel) {
        return AIModelConsts.visionModels.contains(aimodel);
    }

    @Override
    public ChatCompletionDTO createRxtraQsCompletion(ExtraQsForm params) {
        String aimodel = params.getAimodel();

        ChatCompletionDTO completion = new ChatCompletionDTO();
        completion.setModel(aimodel);
        completion.setMaxTokens(4096);

        // 系统提示词
        List<MessageDTO> messages = new ArrayList<MessageDTO>();
        String prompt = Optional.ofNullable(params.getPrompt()).orElse(PromptsConsts.EXTRA_QS_TEMPLATE);
        MessageDTO message = createImgMessage(aimodel, "user", 
                prompt, false,
                "data:image/jpeg;base64," + params.getImgStr());
        messages.add(message);

        completion.setMessages(messages);
        return completion;
    }

    @Override
    public ChatCompletionDTO createDocAreaResponseFormatCompletion(DocCorrectRecordDTO record, JSONObject areaObj,
            JSONObject areaRes) {
        String aimodel = record.getAimodel();
        ChatCompletionDTO completion = new ChatCompletionDTO();
        completion.setModel(aimodel);
        completion.setMaxTokens(4096);
        // 系统提示词
        List<MessageDTO> messages = new ArrayList<MessageDTO>();
        String sysMsg = PromptsConsts.DOC_CORRECT_RESPONSE_FORMAT;
        if (Objects.nonNull(record.getConfig())) {
            sysMsg = record.getConfig().getConfigObj().getStr("prompt", sysMsg);
        }
        setSystemMessage(messages, aimodel, sysMsg);
        // 批改逻辑
        StringBuilder content = new StringBuilder();
        JSONArray questions = areaObj.getJSONArray("questions");
        questions.forEach(qs -> {
            JSONObject question = (JSONObject) qs;
            content.append(String.format(PromptsConsts.DOC_CORRECT_RESPONSE_FORMAT_TEMPLATE,
                    question.getStr("question"), question.getStr("qsInfo", ""),
                    question.getStr("answer"), question.getStr("score", "未定义"),
                    question.getStr("scorePoints", "未定义")));
            content.append("\n");
        });
        log.info("request content: {} {} \n {}", record.getId(), record.getDocname(), content.toString());
        String areaImg = areaObj.getStr("areaImg");
        MessageDTO message;
        if (Base64.isBase64(areaImg)) {
            message = createImgMessage(aimodel, "user", content.toString(), false, areaImg);
        } else {
            String areaImgUrl = areaImg;
            if (!areaImg.startsWith("http")) {
                areaImgUrl = String.format("%s%s", serverUrl, areaImg);
            }
            message = createImgMessage(aimodel, "user", content.toString(), true, areaImgUrl);
        }
        messages.add(message);
        // 结构化输出
        JSONObject responseFormat = JSONUtil.parseObj(PromptsConsts.DOC_CORRECT_RESPONSE_FORMAT_SCHEMA1);
        JSONObject schemaProps = new JSONObject();
        JSONArray requiredProps = new JSONArray();
        JSONObject schemaPropObj = JSONUtil.parseObj(PromptsConsts.DOC_CORRECT_RESPONSE_FORMAT_SCHEMA2);
        for (int i = 0; i < questions.size(); i++) {
            String propName = "题目" + (i + 1);
            requiredProps.add(propName);
            schemaProps.set(propName, schemaPropObj);
        }
        JSONUtil.putByPath(responseFormat, "json_schema.schema.properties", schemaProps);
        JSONUtil.putByPath(responseFormat, "json_schema.schema.required", requiredProps);

        completion.setMessages(messages);
        completion.setResponseFormat(responseFormat);
        return completion;
    }

    @Override
    public ChatCompletionDTO createWriteQsCompletion1Request(DocCorrectRecordDTO record, JSONObject areaObj,
            JSONObject areaRes) {
        String aimodel = record.getTask().getAimodel();
        JSONArray questions = areaObj.getJSONArray("questions");
        ChatCompletionDTO completion = new ChatCompletionDTO();
        completion.setModel(aimodel);
        if (aimodel.equals(AIModelConsts.GPT_4O_20240806)) {
            completion.setMaxTokens(AIModelMaxTokenConsts.GPT_4O_20240806);
        } else{
            completion.setMaxTokens(4096);
        }
        List<MessageDTO> messages = new ArrayList<MessageDTO>();
        String sysMsg = PromptsConsts.DOC_CORRECT_WRITE_QS_SYSTEM_PROMPT;
        setSystemMessage(messages, aimodel, String.format(sysMsg, questions.getJSONObject(0).getStr("score", "1")));
        // 图片
        String areaImg = areaObj.getStr("areaImg");
        String areaImgUrl = String.format("%s%s", serverUrl, areaImg);
        MessageDTO message = createImgMessage(aimodel, "user", "", true, areaImgUrl);
        messages.add(message);
        // 结构化输出
        JSONObject responseFormat = JSONUtil.parseObj(PromptsConsts.DOC_CORRECT_WRITE_QS_RESPONSE_FORMAT);
        completion.setMessages(messages);
        completion.setResponseFormat(responseFormat);
        return completion;
    }

    @Override
    public ChatCompletionDTO createWriteQsCompletion2Request(DocCorrectRecordDTO record, JSONObject areaObj, String essay, String gradeName, String title) {
        String aimodel = record.getTask().getAimodel();
        JSONArray questions = areaObj.getJSONArray("questions");
        ChatCompletionDTO completion = new ChatCompletionDTO();
        completion.setModel(aimodel);
        if (aimodel.equals(AIModelConsts.GPT_4O_20240806)) {
            completion.setMaxTokens(AIModelMaxTokenConsts.GPT_4O_20240806);
        } else{
            completion.setMaxTokens(4096);
        }

        completion.setModel(aimodel);
        completion.setMaxTokens(4096);
        List<MessageDTO> messages2 = new ArrayList<MessageDTO>();
        String sysMsg = PromptsConsts.DOC_CORRECT_WRITE_QS_SYSTEM_PROMPT2;
        setSystemMessage(messages2, aimodel, String.format(sysMsg, gradeName, title ,questions.getJSONObject(0).getStr("score", "1"), essay));
        completion.setMessages(messages2);
        JSONObject responseFormat2 = JSONUtil.parseObj(PromptsConsts.DOC_CORRECT_WRITE_QS_RESPONSE_FORMAT2);
        completion.setResponseFormat(responseFormat2);
        return completion;
    }

    @Override
    public ChatCompletionDTO createScoreCompletion(DocCorrectRecordDTO record, JSONObject areaObj,
            JSONObject areaRes) {
        String aimodel = record.getTask().getAimodel();
        JSONArray questions = areaObj.getJSONArray("questions");
        ChatCompletionDTO completion = new ChatCompletionDTO();
        completion.setModel(aimodel);
        completion.setMaxTokens(4096);

        List<MessageDTO> messages = new ArrayList<MessageDTO>();
        // 系统提示词
        String sysMsg = PromptsConsts.DOC_CORRECT_SCORE_SYSTEM_PROMPT;
        setSystemMessage(messages, aimodel, String.format(sysMsg));
        // 图片
        String areaImg = areaObj.getStr("areaImg");
        String areaImgUrl = String.format("%s%s", serverUrl, areaImg);
        MessageDTO message = createImgMessage(aimodel, "user", "", true, areaImgUrl);
        messages.add(message);
        // 结构化输出
        JSONObject responseFormat = JSONUtil.parseObj(PromptsConsts.DOC_CORRECT_SCORE_RESPONSE_FORMAT);

        completion.setMessages(messages);
        completion.setResponseFormat(responseFormat);
        return completion;
    }

    @Override
    public ChatCompletionDTO createEssayAnalyticalReportCompletion(List<DocCorrectRecordDTO> records, DocCorrectRecordDTO record,String scoreSituation, String gradeName) {
        String aimodel = record.getTask().getAimodel();
        ChatCompletionDTO completion = new ChatCompletionDTO();
        completion.setModel(aimodel);
        completion.setMaxTokens(4096);

        List<MessageDTO> messages = new ArrayList<MessageDTO>();
        String sysMsg = PromptsConsts.DOC_CORRECT_ESSAY_REPORT_PROMPT;
        // 删除多余的减少token消耗
        records.forEach(item -> {
            item.setReviewed(null);
        });
        setSystemMessage(messages, aimodel, String.format(sysMsg, scoreSituation, gradeName, JSONUtil.toJsonStr(records)));
        MessageDTO message = createImgMessage(aimodel, "user", "", true, null);
        messages.add(message);
        JSONObject responseFormat = JSONUtil.parseObj(PromptsConsts.DOC_CORRECT_ESSAY_REPORT_RESPONSE_FORMAT);
        completion.setMessages(messages);
        completion.setResponseFormat(responseFormat);
        return completion;
    }

}
