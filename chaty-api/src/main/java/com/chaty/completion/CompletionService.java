package com.chaty.completion;

import java.util.List;
import java.util.Map;

import com.chaty.dto.ChatCompletionDTO;
import com.chaty.dto.CorrectQsDTO;
import com.chaty.dto.DocCorrectRecordDTO;
import com.chaty.dto.OrgQuestionDTO;
import com.chaty.entity.OrgCorrectResult;
import com.chaty.form.ExtraQsForm;

import cn.hutool.json.JSONObject;

public interface CompletionService {

    void correctRecordArea(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes);

    List<CorrectQsDTO> extraQs(ExtraQsForm params);

    void correctAnswerCard(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes);

    /**
     * 写作题批改
     */
    Map<String, Object> correctWriteQs(String aimodel, ChatCompletionDTO completion);

    void correctWriteQsTwiceMergers(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes);

    String createEssayAnalyticalReportCompletion(List<DocCorrectRecordDTO> records, DocCorrectRecordDTO param, String scoreSituation, String gradeName);

    /**
     * 老师分数识别
     */
    void correctScoreArea(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes);

    OrgCorrectResult correctOrgQs(OrgQuestionDTO params);

}