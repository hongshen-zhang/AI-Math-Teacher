package com.chaty.completion;

import java.util.*;
import java.math.BigDecimal;

import javax.annotation.Resource;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;

import com.chaty.dto.ChatCompletionDTO;
import com.chaty.dto.CorrectQsDTO;
import com.chaty.dto.DocCorrectRecordDTO;
import com.chaty.dto.DocCorrectTaskDTO;
import com.chaty.dto.OrgQuestionDTO;
import com.chaty.entity.OrgCorrectResult;
import com.chaty.enums.AIModelConsts;
import com.chaty.exception.BaseException;
import com.chaty.exception.RetryException;
import com.chaty.form.ExtraQsForm;
import com.chaty.service.BasicAiService;
import com.chaty.util.FileUtil;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CompletionServiceImpl implements CompletionService {

    @Resource
    private Set<CompletionCreator> creators;
    @Resource
    private Set<BasicAiService> services;
    @Resource
    private Set<CompletionResResolver> resolvers;
    @Resource
    private AnswerCardReviewService answerCardReviewService;

    @Retryable(value = RetryException.class, maxAttempts = 5, listeners = {"docCorrectRetryListener"})
    @Override
    public void correctRecordArea(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes) {
        String aimodel = record.getAimodel();
        DocCorrectTaskDTO task = record.getTask();
        Boolean responseFormat = task.getResponseFormat();
        CompletionCreator creator = getCreator(aimodel);
        ChatCompletionDTO completion = null;
        if (responseFormat) {
            completion = creator.createDocAreaResponseFormatCompletion(record, areaObj, areaRes);
        } else {
            completion = creator.createDocAreaCompletion(record, areaObj, areaRes);
        }
        completion.setTemperature(record.getTemperature()); // 设置模型 temperature 参数
        JSONArray questions = areaObj.getJSONArray("questions");
        log.info("completionRes aimodel:{}", aimodel);
        Map<String, Object> completionRes = chatCompletion(aimodel, completion);

        CompletionResResolver resolver = getResover(aimodel);
        JSONObject resolvedRes = null;
        try {
            if (responseFormat) {
                resolvedRes = resolver.resolveDocAreaResponseFormatRes(record, completionRes);
            } else {
                resolvedRes = resolver.resolveDocAreaRes(record, completionRes);
            }
        } catch (Exception e) {
            log.error("批改结果解析失败: {}", resolvedRes, e);
            throw new RetryException("批改结果解析失败!", e);
        }
        JSONArray reviewed = resolvedRes.getJSONArray("reviewed");
        if (reviewed.size() != questions.size()) {
            log.error("批改结果无法解析: {}", reviewed);
            throw new RetryException("批改结果无法解析!");
        }
        // 批改结果验证
        checkReviewed(areaObj, reviewed);

        areaRes.putAll(resolvedRes);
    }

    @Retryable(value = RetryException.class, maxAttempts = 5, listeners = {"docCorrectRetryListener"})
    @Override
    public void correctScoreArea(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes) {
        JSONArray questions = areaObj.getJSONArray("questions");
        Float score = 0F;
        if (!questions.isEmpty()) {
            JSONObject qs = questions.getJSONObject(0);
            score = qs.getFloat("score", 0F);
        }

        String aimodel = record.getAimodel();
        CompletionCreator creator = getCreator(aimodel);
        ChatCompletionDTO completion = creator.createScoreCompletion(record, areaObj, areaRes);

        completion.setTemperature(record.getTemperature()); // 设置模型 temperature 参数
        completion.setModel(aimodel);
        log.info("chatCompletion aimodel: {}", aimodel);
        Map<String, Object> completionRes = chatCompletion(aimodel, completion);

        CompletionResResolver resolver = getResover(aimodel);
        JSONObject resolvedRes = null;
        try {
            resolvedRes = resolver.resolveScoreRes(record, completionRes, score);
        } catch (Exception e) {
            throw new RetryException("批改结果解析失败!", e);
        }
        areaRes.putAll(resolvedRes);
    }

    private void checkReviewed(JSONObject areaObj, JSONArray reviewed) {
        // int opinion = areaObj.getInt("opinion", 1);
        // if (opinion != 1) {
        //     return;
        // }

        // 分数校验，最大分数为题目分数
        JSONArray questions = areaObj.getJSONArray("questions");
        for (int i = 0; i < reviewed.size(); i++) {
            JSONObject qsObj = questions.getJSONObject(i);
            JSONObject review = reviewed.getJSONObject(i);
            if (Objects.nonNull(review.get("scored"))) {
                review.putByPath(String.format("[%s].scored", i), 
                    NumberUtil.min(qsObj.getBigDecimal("score", BigDecimal.ZERO), review.getBigDecimal("scored", BigDecimal.ZERO)));
            }
        }

        RetryContext context = RetrySynchronizationManager.getContext();
        if (context != null) {
            int retryCount = context.getRetryCount();
            log.info("retryCount={}", retryCount);
            if (retryCount > 3) {
                return; // 重试次数过多,不再校验
            }
        }

        for (int i = 0; i < reviewed.size(); i++) {
            Integer opinion = areaObj.getByPath(String.format("questions[%s].opinion", i), Integer.class);
            opinion = Optional.ofNullable(opinion).orElse(2); // 默认主观,不需要校验
            if (opinion != 1) {
                continue;
            }
            JSONObject qsRes = reviewed.getJSONObject(i);
            boolean isTrue = qsRes.getStr("isCorrect").equals("Y");
            String studentAnswer = qsRes.getStr("studentAnswer", "");
            String correctAnswer = areaObj.getByPath(String.format("questions[%s].answer", i), String.class);
            boolean actTrue = Objects.equals(correctAnswer, studentAnswer);
            if (isTrue != actTrue) {
                log.error("批改结果验证失败: qsIdx={}, studentAnswer={}, correctAnswer={}, actTrue={}", i, studentAnswer, correctAnswer, actTrue);
                throw new RetryException("批改结果验证失败!");
            }
        }
    }

    public CompletionCreator getCreator(String aimodel) {
        for (CompletionCreator creator : creators) {
            if (creator.isSupported(aimodel)) {
                return creator;
            }
        }
        return null;
    }

    public BasicAiService getService(String aimodel) {
        for (BasicAiService service : services) {
            if (service.isSupport(aimodel)) {
                return service;
            }
        }
        return null;
    }

    public CompletionResResolver getResover(String aimodel) {
        for (CompletionResResolver resolver : resolvers) {
            if (resolver.isSupported(aimodel)) {
                return resolver;
            }
        }
        return null;
    }

    @Retryable(value = RetryException.class, maxAttempts = 10)
    @Override
    public List<CorrectQsDTO> extraQs(ExtraQsForm params) {
        String aimodel = params.getAimodel();
        CompletionCreator creator = getCreator(aimodel);
        ChatCompletionDTO completion = creator.createRxtraQsCompletion(params);

        Map<String, Object> completionRes = chatCompletion(aimodel, completion);

        CompletionResResolver resolver = getResover(aimodel);
        List<CorrectQsDTO> resolvedRes = null;
        try {
            resolvedRes = resolver.resolveRxtraQsRes(completionRes);
        } catch (Exception e) {
            log.error("批改结果解析失败", e);
            throw new RetryException("批改结果解析失败!", e);
        }

        return resolvedRes;
    }

    @Retryable(value = RetryException.class, maxAttempts = 10)
    private Map<String, Object> chatCompletion(String aimodel, ChatCompletionDTO completion) {
        BasicAiService service = getService(aimodel);
        Map<String, Object> completionRes = null;
        try {
            completionRes = service.chatForCompletion(completion);
        } catch (RetryException e) {
            log.error("请求模型失败", e);
            throw new RetryException("请求模型失败!", e);
        } catch (Exception e) {
            log.error("请求模型失败", e);
            throw new BaseException("请求模型失败!", e);
        }
        return completionRes;
    }

    @Override
    public void correctAnswerCard(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes) {
        JSONArray questions = areaObj.getJSONArray("questions");
        JSONArray reviewed = new JSONArray();
        for (int qsIdx = 0; qsIdx < questions.size(); qsIdx++) {
            JSONObject qs = questions.getJSONObject(qsIdx);
            JSONObject reviewedQs = answerCardQsReview(record, areaRes.getInt("areaIdx"), qsIdx, qs);
            reviewed.add(reviewedQs);
        }
        areaRes.set("reviewed", reviewed);
    }

    public JSONObject answerCardQsReview(DocCorrectRecordDTO record, Integer areaIdx, Integer qsIdx, JSONObject qs) {
        JSONObject optionArea = qs.getJSONObject("optionArea");        
        String qsOptionImg = FileUtil.INSTANCE.docAreaImg(record.getDocParh(), optionArea.getInt("x"), optionArea.getInt("y"),
                optionArea.getInt("width"), optionArea.getInt("height"));
        log.info("answer card review: {} {} {} {} {}", record.getId(), record.getDocname(), areaIdx, qsIdx, qsOptionImg);
        JSONObject reviewRes = answerCardReviewService.reviewAnswerCard(FileUtil.INSTANCE.absPath(qsOptionImg), qs);
        
        JSONArray studentAnswer = reviewRes.getJSONArray("studentAnswer");
        JSONArray answer = qs.getJSONArray("answer");
        String isCorrect = "N";
        if (answer.size() == studentAnswer.size()) {
            Set<Integer> stuAnswer = new HashSet<>(studentAnswer.toList(Integer.class));
            Set<Integer> corAnswer = new HashSet<>(answer.toList(Integer.class));
            if (stuAnswer.equals(corAnswer)) {
                isCorrect = "Y";
            }
        }
        reviewRes.set("isCorrect", isCorrect);
        return reviewRes;
    }

    @Override
    public Map<String, Object> correctWriteQs(String aimodel, ChatCompletionDTO completion) {
        return chatCompletion(aimodel, completion);
    }

    @Retryable(value = RetryException.class, maxAttempts = 5, listeners = {"docCorrectRetryListener"})
    @Override
    public void correctWriteQsTwiceMergers(DocCorrectRecordDTO record, JSONObject areaObj, JSONObject areaRes) {
        // 作文批改无法使用3倍批改
        if (StrUtil.isBlank(record.getAimodel()) || record.getAimodel().equals(AIModelConsts.GPT_4O_20240806_3)) {
            record.setAimodel(AIModelConsts.GPT_4O_20240806);
        }
        if (StrUtil.isBlank(record.getTask().getAimodel()) || record.getTask().getAimodel().equals(AIModelConsts.GPT_4O_20240806_3)) {
            DocCorrectTaskDTO taskDTO = record.getTask();
            taskDTO.setAimodel(AIModelConsts.GPT_4O_20240806);
            record.setTask(taskDTO);
        }

        String aimodel = record.getAimodel();
        CompletionCreator creator = getCreator(aimodel);
        ChatCompletionDTO completions1Request = creator.createWriteQsCompletion1Request(record, areaObj, areaRes);
        JSONArray questions = areaObj.getJSONArray("questions");
        // 第一次请求
        Map<String, Object> completionRes = correctWriteQs(aimodel, completions1Request);
        // 第二次次请求
        String essay = resolveDocWriteQsEssay(completionRes);
        JSONObject essayConfigObj  = null;
        if (!questions.isEmpty()) {
            essayConfigObj = questions.getJSONObject(0).getJSONObject("qsInfo");
        }
        String gradeName = "初一";
        String title = "";
        try {
            if (essayConfigObj != null) {
                gradeName = essayConfigObj.getStr("grade");
            }
            if (StrUtil.isBlank(gradeName)) {
                gradeName = "初一";
            }
        } catch (Exception e) {
            log.error("获取班级名称失败", e);
        }
        try {
            if (essayConfigObj != null) {
                title = essayConfigObj.getStr("essayTitle");
            }
        } catch (Exception e) {
            log.error("获取班级名称失败", e);
        }
        ChatCompletionDTO completions2Request = creator.createWriteQsCompletion2Request(record, areaObj, essay, gradeName, title);
        Map<String, Object> completionRes2Request = correctWriteQs(aimodel, completions2Request);
        // 合并
        String rawResp1Request = (String) completionRes.get("$response");
        JSONObject response1Request = JSONUtil.parseObj(rawResp1Request);
        String rawResp2Request = (String) completionRes2Request.get("$response");
        JSONObject response2Request = JSONUtil.parseObj(rawResp2Request);
        response1Request.putAll(response2Request);
        completionRes.put("$response", JSONUtil.toJsonStr(response1Request));

        CompletionResResolver resolver = getResover(aimodel);
        JSONObject resolvedRes = null;
        try {
            resolvedRes = resolver.resolveDocWriteQsRes(record, completionRes, Float.valueOf(questions.getJSONObject(0).getStr("score", "1")));
        } catch (Exception e) {
            log.error("批改结果解析失败: {}", resolvedRes, e);
            throw new RetryException("批改结果解析失败!", e);
        }
        JSONArray reviewed = resolvedRes.getJSONArray("reviewed");
        if (reviewed.size() != questions.size()) {
            log.error("批改结果无法解析: {}", reviewed);
            throw new RetryException("批改结果无法解析!");
        }
        // 批改结果验证
        checkReviewed(areaObj, reviewed);

        areaRes.putAll(resolvedRes);
    }

    @Retryable(value = RetryException.class, maxAttempts = 5, listeners = {"docCorrectRetryListener"})
    @Override
    public String createEssayAnalyticalReportCompletion(List<DocCorrectRecordDTO> records, DocCorrectRecordDTO record,String scoreSituation, String gradeName) {
        // 作文批改无法使用3倍批改
        if (StrUtil.isBlank(record.getAimodel()) || record.getAimodel().equals(AIModelConsts.GPT_4O_20240806_3)) {
            record.setAimodel(AIModelConsts.GPT_4O_20240806);
        }
        if ( record.getTask() == null || StrUtil.isBlank(record.getTask().getAimodel()) || record.getTask().getAimodel().equals(AIModelConsts.GPT_4O_20240806_3)) {
            DocCorrectTaskDTO taskDTO = record.getTask();
            if (taskDTO == null) {
                taskDTO = new DocCorrectTaskDTO();
            }
            taskDTO.setAimodel(AIModelConsts.GPT_4O_20240806);
            record.setTask(taskDTO);
        }

        String aimodel = record.getAimodel();
        CompletionCreator creator = getCreator(aimodel);
        ChatCompletionDTO completionsRequest = creator.createEssayAnalyticalReportCompletion(records, record,scoreSituation, gradeName);
        Map<String, Object> completionRes = correctWriteQs(aimodel, completionsRequest);

        CompletionResResolver resolver = getResover(aimodel);
        String resolvedRes = "";
        try {
            resolvedRes = resolver.resolveEssayAnalyticalReportDocRes(completionRes);
        } catch (Exception e) {
            log.error("作文分析结果解析失败: {}", resolvedRes, e);
            throw new RetryException("作文分析结果解析失败!", e);
        }
        return resolvedRes;
    }

    private String resolveDocWriteQsEssay(Map<String, Object> completionRes) {
        String rawResp = (String) completionRes.get("$response");
        JSONObject content = JSONUtil.parseObj(rawResp);
        return content.getStr("学生作文全文");
    }

    @Retryable(value = RetryException.class, maxAttempts = 5)
    @Override
    public OrgCorrectResult correctOrgQs(OrgQuestionDTO params) {
        String aimodel = AIModelConsts.GPT_4O_MINI;
        boolean responseFormat = false;
        float temperature = 0f;

        DocCorrectRecordDTO record = new DocCorrectRecordDTO();
        record.setAimodel(aimodel);
        JSONObject areaObj = new JSONObject();
        JSONArray questions = new JSONArray();
        questions.add(new JSONObject()
                .set("question", params.getQuestion())
                .set("qsInfo", params.getQuestionInfo())
                .set("answer", params.getCorrectAnswer())
                .set("score", params.getScore()));
        areaObj.set("questions", questions);
        JSONObject areaRes = new JSONObject();
        areaRes.set("areaImg", params.getCorrectImage());

        CompletionCreator creator = getCreator(aimodel);
        ChatCompletionDTO completion = null;
        if (responseFormat) {
            completion = creator.createDocAreaResponseFormatCompletion(record, areaObj, areaRes);
        } else {
            completion = creator.createDocAreaCompletion(record, areaObj, areaRes);
        }
        completion.setTemperature(temperature); // 设置模型 temperature 参数

        Map<String, Object> completionRes = chatCompletion(aimodel, completion);

        CompletionResResolver resolver = getResover(aimodel);
        JSONObject resolvedRes = null;
        try {
            if (responseFormat) {
                resolvedRes = resolver.resolveDocAreaResponseFormatRes(record, completionRes);
            } else {
                resolvedRes = resolver.resolveDocAreaRes(record, completionRes);
            }
        } catch (Exception e) {
            log.error("批改结果解析失败: {}", resolvedRes, e);
            throw new RetryException("批改结果解析失败!", e);
        }

        JSONArray reviewed = resolvedRes.getJSONArray("reviewed");
        if (Objects.isNull(reviewed) || reviewed.isEmpty()) {
            throw new RetryException("批改结果解析失败!");
        }
        JSONObject reviewRes = reviewed.getJSONObject(0);
        OrgCorrectResult result = new OrgCorrectResult();
        result.setStudentAnswer(reviewRes.getStr("studentAnswer"));
        result.setCorrect(Objects.equals(reviewRes.getStr("isCorrect", "Y"), "Y"));
        result.setScore(reviewRes.getBigDecimal("scored", BigDecimal.ZERO));
        result.setComment(reviewRes.getStr("review"));

        return result;
    }

}
