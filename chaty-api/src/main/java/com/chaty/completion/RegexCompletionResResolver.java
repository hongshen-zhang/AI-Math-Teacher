package com.chaty.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import com.chaty.api.essayScoring.EssayScoreApi;
import com.chaty.api.essayScoring.EssayScoreBaseResponse;
import org.springframework.stereotype.Service;

import com.chaty.dto.CorrectQsDTO;
import com.chaty.dto.DocCorrectRecordDTO;
import com.chaty.enums.AIModelConsts;
import com.chaty.exception.BaseException;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

@Slf4j
@Service
public class RegexCompletionResResolver implements CompletionResResolver {

    @Resource
    private EssayScoreApi essayScoreApi;

    @Override
    public JSONObject resolveDocAreaRes(DocCorrectRecordDTO record, Map<String, Object> completionRes) {
        String rawResp = (String) completionRes.get("$response");
        log.info("rawResp: {} {} \n {}", record.getId(), record.getDocname(), rawResp);
        String regexTemplate = "(?<=-\\s?%s\\s?\\n---\\s?\\n)[\\d\\D]*?(?=(\\n---\\s?(\\n-\\s{1,3}.*|$|```)|\\n-\\s{1,3}.*|```|$))";
        JSONObject res = new JSONObject();
        JSONArray reviewed = new JSONArray();

        Map<String, String> regKeyMap = MapUtil.builder("学生手写回答", "studentAnswer")
                .put("批改结果", "isCorrect")
                .put("答案评价", "review")
                .put("分数", "scored")
                .build();
        regKeyMap.forEach((k, v) -> {
            String regex = String.format(regexTemplate, k);
            List<String> matched = ReUtil.findAll(regex, rawResp, 0);
            if (matched.isEmpty()) {
                throw new BaseException("批改结果解析失败.");
            }
            String[] groups = matched.get(0).split("---");
            for (int i = 0; i < groups.length; i++) {
                JSONObject r = reviewed.getJSONObject(i);
                if (Objects.isNull(r)) {
                    r = new JSONObject();
                    reviewed.add(r);
                }
                r.set(v, groups[i].trim());
            }
        });

        reviewed.forEach(r -> {
            JSONObject obj = (JSONObject) r;
            if (Objects.isNull(obj.get("isCorrect"))) {
                throw new BaseException("批改结果解析失败.");
            }
            if (!NumberUtil.isNumber(obj.getStr("scored", "0"))) {
                throw new BaseException("批改结果解析失败.");
            }
            if (Objects.isNull(obj.get("scored"))) {
                obj.set("scored", 0);
            }
        });

        res.set("reviewed", reviewed);
        return res;
    }

    @Override
    public boolean isSupported(String aimodel) {
        return !AIModelConsts.functionCallModels.contains(aimodel);
    }

    @Override
    public List<CorrectQsDTO> resolveRxtraQsRes(Map<String, Object> completionRes) {
        String rawResp = (String) completionRes.get("$response");
        String regexTemplate = "(?<=##\\s?%s\\s?\\n)[\\d\\D]*?(?=((\\n(##|---)|$)))";

        Map<String, String> regKeyMap = MapUtil.builder("学生手写回答", "studentAnswer")
                .put("题目", "question")
                .put("答案", "answer")
                .put("答案的相关信息", "questionInfo")
                .build();
        List<CorrectQsDTO> qss = new ArrayList<>();
        regKeyMap.forEach((k, v) -> {
            String regex = String.format(regexTemplate, k);
            List<String> matched = ReUtil.findAll(regex, rawResp, 0);
            for (int index = 0; index < matched.size(); index++) {
                if (qss.size() == index) {
                    qss.add(new CorrectQsDTO());
                }
                CorrectQsDTO qs = qss.get(index);
                BeanUtil.setProperty(qs, v, matched.get(index).trim());
            }
        });
        return qss;
    }

    @Override
    public JSONObject resolveDocAreaResponseFormatRes(DocCorrectRecordDTO record, Map<String, Object> completionRes) {
        String rawResp = (String) completionRes.get("$response");
        JSONObject content = JSONUtil.parseObj(rawResp);
        log.info("rawResp: {} {} \n {}", record.getId(), record.getDocname(), content);
        JSONArray reviewed = new JSONArray();
        JSONObject res = new JSONObject();

        content.forEach((qsId, qsRes) -> {
            JSONObject qsResObj = (JSONObject) qsRes;
            JSONObject qsReviewed = new JSONObject();
            qsReviewed.set("studentAnswer", qsResObj.getStr("学生答案"));
            qsReviewed.set("isCorrect", qsResObj.getBool("是否正确") ? "Y" : "N");
            qsReviewed.set("review", qsResObj.getStr("评价"));
            qsReviewed.set("scored", qsResObj.getStr("得分"));
            reviewed.add(qsReviewed);
        });

        res.set("reviewed", reviewed);
        return res;
    }

    @Override
    public JSONObject resolveScoreRes(DocCorrectRecordDTO record, Map<String, Object> completionRes, Float allScore) {
        Float score = 0F;
        Float completionResScore = 0F;
        String rawResp = (String) completionRes.get("$response");
        log.info("score completionRes: {}", completionRes);
        try {
            JSONObject response = JSONUtil.parseObj(rawResp);
            // 获取分数字段
            String scoreString = (String) response.get("分数");
            if (scoreString != null) {
                try {
                    completionResScore = Float.parseFloat(scoreString);
                    score = allScore + completionResScore;
                } catch (NumberFormatException e) {
                    log.error("Invalid score format: {}", scoreString, e);
                }
            } else {
                log.error("Score field is missing.");
            }
        } catch (Exception e) {
            log.error("解析分数失败", e);
            // 识别失败或没有将分数设置为最高分
            score = allScore;
        }

        if (score < 0) {
            score = 0F;
        } else if (score > allScore) {
            score = allScore;
        }
        JSONArray reviewed = new JSONArray();
        JSONObject qsReviewed = new JSONObject();
        qsReviewed.set("studentAnswer", score);
        qsReviewed.set("isCorrect", "Y");
        qsReviewed.set("review", "识别老师分数，分数为" + completionResScore);
        qsReviewed.set("scored", score);
        qsReviewed.set("scoreType", 4);
        qsReviewed.set("isScorePoint", 2);
        reviewed.add(qsReviewed);
        JSONObject res = new JSONObject();
        res.set("reviewed", reviewed);
        JSONArray reviewList = new JSONArray();
        reviewList.add(reviewed);
        res.set("reviewList", reviewList);
        return res;
    }


    @Override
    public JSONObject resolveDocWriteQsRes(DocCorrectRecordDTO record, Map<String, Object> completionRes, Float allScore) {
        String rawResp = (String) completionRes.get("$response");
        JSONObject content = JSONUtil.parseObj(rawResp);
        log.info("rawResp: {} {} \n {}", record.getId(), record.getDocname(), content);

        JSONArray reviewed = new JSONArray();
        JSONObject res = new JSONObject();
        JSONObject qsReviewed = new JSONObject();
        qsReviewed.set("studentAnswer", content.getStr("学生作文全文"));
        qsReviewed.set("isCorrect", "Y");
        qsReviewed.set("review", content);
        qsReviewed.set("scored", content.get("评分"));
        qsReviewed.set("isEssay", true);
        qsReviewed.set("answer", content.getStr("作文重写"));
        qsReviewed.set("studentName", content.getStr("学生姓名"));
        // 分数使用essayScore获取
        try {
            JSONObject text = new JSONObject();
            text.set("text", content.getStr("学生作文全文"));
            // github模型打分
            EssayScoreBaseResponse response = essayScoreApi.getScore(JSONUtil.toJsonStr(text));
            Integer score = response.getScore2TotalScore(allScore);
            // 大模型打分占85%，github开源模型占15%，由于分数偏低，增加3-8分的随机
            score = (int) Math.min(allScore, (int) Math.ceil(score * 0.15 + (Float) qsReviewed.get("scored") * 0.85) + ThreadLocalRandom.current().nextInt(3, 8));
            if (score <= 60) {
                score = 70 + getRandomInt();
            }
            qsReviewed.set("scored", score);
        } catch (Exception e) {
            // 默认70分
            qsReviewed.set("scored", 70 + getRandomInt());
            log.error("获取分数失败", e);
        }

        try {
            // 重写分数也打分
            JSONObject text = new JSONObject();
            text.set("text", content.getStr("作文重写"));
            EssayScoreBaseResponse response = essayScoreApi.getScore(JSONUtil.toJsonStr(text));
            Integer scoreRewrite = response.getScore2TotalScore(allScore);
            // 大模型打分占85%，github开源模型占15%
            scoreRewrite = (int) Math.ceil(scoreRewrite * 0.15 + (Float) content.get("重写后的分数") * 0.85 + ThreadLocalRandom.current().nextInt(3, 8));
            // 重写后的分数要比之前的高
            Integer score = qsReviewed.getInt("scored");
            if (scoreRewrite < score) {
                scoreRewrite = (int) Math.min(allScore, score + Math.abs(score - scoreRewrite));
            } else if (scoreRewrite.equals(score) && scoreRewrite != 100) {
                scoreRewrite = (int) Math.min(allScore, score + ThreadLocalRandom.current().nextInt(1, 10));
            }
            content.set("重写后的分数", scoreRewrite);
        } catch (Exception e) {
            Integer score = qsReviewed.getInt("scored");
            content.set("重写后的分数", Math.min(allScore, score + ThreadLocalRandom.current().nextInt(0, 10)));
            log.error("获取重写分数失败", e);
        }

        completionRes.put("$response", JSONUtil.toJsonStr(content));
        reviewed.add(qsReviewed);
        res.set("reviewed", reviewed);
        return res;
    }

    public static int getRandomInt() {
        return ThreadLocalRandom.current().nextInt(-10, 21); // 上限是排他的，所以设为21
    }

    @Override
    public String resolveEssayAnalyticalReportDocRes(Map<String, Object> completionRes) {
        String rawResp = (String) completionRes.get("$response");
        JSONObject content = JSONUtil.parseObj(rawResp);
        return content.getStr("报告");
    }
}