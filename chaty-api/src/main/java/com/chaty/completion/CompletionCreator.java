package com.chaty.completion;

import java.util.List;
import java.util.Objects;

import com.chaty.dto.ChatCompletionDTO;
import com.chaty.dto.DocCorrectRecordDTO;
import com.chaty.dto.MessageDTO;
import com.chaty.enums.AIModelConsts;
import com.chaty.form.ExtraQsForm;
import com.chaty.util.FileUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public interface CompletionCreator {

    boolean isSupported(String aimodel);

    ChatCompletionDTO createDocAreaCompletion(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes);

    ChatCompletionDTO createDocAreaResponseFormatCompletion(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes);

    ChatCompletionDTO createWriteQsCompletion1Request(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes);
    ChatCompletionDTO createWriteQsCompletion2Request(DocCorrectRecordDTO record, JSONObject areaObj, String essay, String gradeName, String title);

    ChatCompletionDTO createScoreCompletion(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes);

    ChatCompletionDTO createEssayAnalyticalReportCompletion(List<DocCorrectRecordDTO> records, DocCorrectRecordDTO param,String scoreSituation, String gradeName);

    default MessageDTO createMessage(String role, String content) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setRole(role);
        messageDTO.setContent(content);
        return messageDTO;
    }

    default void setSystemMessage(List<MessageDTO> messages, String aimodel, String content) {
        if (Objects.equals(AIModelConsts.GPT_4_V, aimodel)) {
            MessageDTO messageDTO = createMessage("system", content);
            messages.add(messageDTO);
        } else {
            // Claude
            messages.add(createMessage("user", content));
            messages.add(createMessage("assistant", "好的, 我会按照您的要求批改试卷。"));
        }
    }

    default MessageDTO createImgMessage(String aimodel, String role, String content, boolean isUrl, String... img) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setRole(role);

        JSONArray contentArr = new JSONArray();
        if (StrUtil.isNotBlank(content)) {
            JSONObject contentObj = new JSONObject();
            contentObj.set("type", "text");
            contentObj.set("text", content);
            contentArr.add(contentObj);
        }
        if (img != null && img.length > 0) {
            for (String imgStr : img) {
                JSONObject imgObj = new JSONObject();
                String base64 = imgStr;
                if (isUrl) {
                    base64 = "data:image/jpeg;base64," + FileUtil.INSTANCE.url2Base64(imgStr);
                }
                if (AIModelConsts.openaiVisionModels.contains(aimodel)) {
                    imgObj.set("type", "image_url");
                    imgObj.set("image_url",
                            JSONUtil.createObj().set("url", base64).set("detail", "high"));
                } else {
                    imgObj.set("type", "img");
                    JSONObject sourceObj = new JSONObject();
                    sourceObj.set("type", "base64");
                    sourceObj.set("media_type", "image/jpeg");
                    sourceObj.set("data", base64);
                    imgObj.set("source", sourceObj);
                }
                contentArr.add(imgObj);
            }
        }

        messageDTO.setContent(contentArr);
        return messageDTO;
    }

    ChatCompletionDTO createRxtraQsCompletion(ExtraQsForm params);

}