package com.chaty.completion;

import java.util.List;
import java.util.Map;

import com.chaty.dto.CorrectQsDTO;
import com.chaty.dto.DocCorrectRecordDTO;

import cn.hutool.json.JSONObject;

public interface CompletionResResolver {

    JSONObject resolveDocAreaRes(DocCorrectRecordDTO record, Map<String, Object> completionRes);

    JSONObject resolveDocAreaResponseFormatRes(DocCorrectRecordDTO record, Map<String, Object> completionRes);

    boolean isSupported(String aimodel);

    List<CorrectQsDTO> resolveRxtraQsRes(Map<String, Object> completionRes);

    JSONObject resolveDocWriteQsRes(DocCorrectRecordDTO record, Map<String, Object> completionRes, Float score);

    String resolveEssayAnalyticalReportDocRes(Map<String, Object> completionRes);

    JSONObject resolveScoreRes(DocCorrectRecordDTO record, Map<String, Object> completionRes, Float allScore);
}
