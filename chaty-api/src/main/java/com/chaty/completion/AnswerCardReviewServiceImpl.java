package com.chaty.completion;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chaty.exception.BaseException;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AnswerCardReviewServiceImpl implements AnswerCardReviewService {

    @Value("${answerCard.executorFile}")
    public String executorFile;
    @Value("${answerCard.pythonCmd:python3}")
    public String pyCmd;

    public String cmdTemplate = "%s %s %s %s %s %s";

    @Override
    public JSONObject reviewAnswerCard(String filePath, JSONObject options) {
        boolean multiple = options.getJSONArray("answer").size() > 1;
        String execRes = RuntimeUtil
                .execForStr(String.format(cmdTemplate, pyCmd, executorFile, filePath, options.getStr("choiceMode"),
                        options.getStr("choiceNum"), multiple ? "--multiple" : ""));
        List<Integer> studentAnswer = Collections.emptyList();
        try {   
            studentAnswer = JSONUtil.parseArray(execRes).stream().map(a -> (Integer) a + 1)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("答题卡批改失败: {}, {}", filePath, execRes, e);
            throw new BaseException(String.format("批改结果: %s", execRes));
        }
        return new JSONObject().set("studentAnswer", JSONUtil.parseArray(studentAnswer));
    }

}
