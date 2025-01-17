package com.chaty.dto;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.chaty.entity.DocCorrectRecord;
import com.chaty.enums.CorrectConfigConsts;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import freemarker.ext.beans.HashAdapter;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class DocCorrectRecordDTO extends DocCorrectRecord {

    private static final Logger log = LoggerFactory.getLogger(DocCorrectRecordDTO.class);
    private PageDTO<DocCorrectRecord> page;

    private String docParh;

    private DocCorrectTaskDTO task;

    private DocCorrectConfigDTO config;

    private Boolean isPreview; // 是否预览

    private Boolean isBatch; // 是否批量

    private Integer areaNum;

    private Integer areaCorrected;

    private Boolean showQsScore = false;

    private String taskName;

    private String[] tagIds;

    private String aimodel;

    private Integer correctTimes;

    private Float temperature;

    private SaveRemoteFileDTO remoteFileProps;

    private PrinterPropsDTO printerProps;

    private String tenantId;

    public JSONArray getReviewedObj() {
        return JSONUtil.parseArray(getReviewed());
    }

    public JSONObject getJSONObj(String jsonStr) {
        return JSONUtil.parseObj(jsonStr);
    }

    public JSONObject getScore() {
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal additionalScore = BigDecimal.ZERO;
        BigDecimal additionalScored = BigDecimal.ZERO;
        BigDecimal scored = BigDecimal.ZERO;
        if (Objects.isNull(config)) {
            return null;
        }
        JSONObject configObj = config.getConfigObj();
        if (!configObj.getBool("score", false)) {
            return JSONUtil.createObj().set("totalScore", totalScore).set("scored", scored);
        }
        Map<String, JSONObject> scoreTypeMap = new HashMap<>();
        JSONArray scoreTypes = configObj.getJSONArray("scoreTypes");
        if (Objects.nonNull(scoreTypes)) {
            scoreTypes.forEach(type -> {
                if (Objects.equals(type, "总分")) {
                    return;
                }
                scoreTypeMap.put(type.toString(), new JSONObject());
            });
        }

        JSONArray reviewed = getReviewedObj();
        JSONArray areas = config.getAreasObj();
        for (int areaIdx = 0; areaIdx < areas.size(); areaIdx++) {
            if (!areas.getJSONObject(areaIdx).getBool("enabled", true)) {
                continue;
            }
            JSONArray questions = areas.getJSONObject(areaIdx).getJSONArray("questions");
            // String status = reviewed.getByPath(String.format("[%s].status", areaIdx),
            // String.class);
            for (int qsIdx = 0; qsIdx < questions.size(); qsIdx++) {
                JSONObject qs = questions.getJSONObject(qsIdx);
                BigDecimal score = qs.getBigDecimal("score");
                int additional = qs.getInt("isAdditional", 1);
                String scoreType = qs.getStr("scoreType");
                String isCorrect = reviewed.getByPath(String.format("[%s].reviewed[%s].isCorrect", areaIdx, qsIdx),
                        String.class);
                boolean isTrue = Objects.equals(Optional.ofNullable(isCorrect).orElse("Y"), "Y"); // 默认为"Y"
                boolean isScorePoint = Objects.equals(qs.getInt("isScorePoint", 1), 2);
                if (additional == 1) {
                    totalScore = totalScore.add(score);
                    if (isScorePoint) {
                        scored = scored.add(NumberUtil.toBigDecimal(reviewed.getByPath(String.format("[%s].reviewed[%s].scored", areaIdx, qsIdx), String.class)));
                    } else {
                        scored = scored.add(isTrue ? score : BigDecimal.ZERO);
                    }
                    if (Objects.nonNull(scoreType) && scoreTypeMap.containsKey(scoreType)) {
                        JSONObject scoreTypeObj = scoreTypeMap.get(scoreType);
                        scoreTypeObj.set("total", scoreTypeObj.getBigDecimal("total", BigDecimal.ZERO)
                                .add(score));
                        scoreTypeObj.set("scored", scoreTypeObj.getBigDecimal("scored", BigDecimal.ZERO)
                                .add(isTrue ? score : BigDecimal.ZERO));
                    }
                } else {
                    additionalScore = additionalScore.add(score);
                    additionalScored = additionalScored.add(isTrue ? score : BigDecimal.ZERO);
                }
            }
        }
        return JSONUtil.createObj()
                .set("totalScore", totalScore)
                .set("scored", scored)
                .set("additionalScore", additionalScore)
                .set("additionalScored", additionalScored)
                .set("scoreTypeMap", scoreTypeMap);
    }

    public String scoreTxt(BigDecimal totalScore, BigDecimal score, String additionalName,
            BigDecimal additionalScore, BigDecimal additionalScored, Map<String, HashAdapter> scoreTypes,
            Integer scoreFormat, List<String> scoreTypesArr) {
        StringBuilder str = new StringBuilder();
        CorrectConfigConsts.ScoreFormat format = CorrectConfigConsts.SCORE_FORMAT_MAP.get(scoreFormat);
        if (scoreTypes == null || scoreTypes.isEmpty()) {
            String scoreTypeStr = scoreTypes.keySet().stream()
                    .map(k -> {
                        HashAdapter typeObj = scoreTypes.get(k);
                        Object scored = Optional.ofNullable(typeObj.get("scored")).orElse(0);
                        Object total = Optional.ofNullable(typeObj.get("total")).orElse(0);
                        return MessageFormat.format(format.getPattern(), k, scored, total,
                                NumberUtil.sub(Convert.toBigDecimal(total), Convert.toBigDecimal(scored)));
                    }).collect(Collectors.joining(format.getDelimiter()));
            str.append(scoreTypeStr);
        } else {
            for (String s : scoreTypesArr) {
                if (s.equals("总分")) {
                    continue;
                }
                HashAdapter typeObj = scoreTypes.get(s);
                BigDecimal scored = Optional.ofNullable(typeObj)
                        .map(value -> (BigDecimal) value.get("scored"))
                        .orElse(BigDecimal.ZERO);

                BigDecimal total = Optional.ofNullable(typeObj)
                        .map(value -> (BigDecimal) value.get("total"))
                        .orElse(BigDecimal.ZERO);

                str.append(MessageFormat.format(format.getPattern(), s, scored, total,
                        NumberUtil.sub(total, scored)));
            }
        }


        if (str.length() > 0) {
            str.append(format.getLastDelimiter());
        }
        str.append(MessageFormat.format(format.getPattern(), "总分", score, totalScore, NumberUtil.sub(totalScore, score)));
        return str.toString();
    }

    public JSONObject mergeScoreType(Map<String, HashAdapter> score1, Map<String, HashAdapter> score2) {
        JSONObject scoreObj1 = JSONUtil.parseObj(score1);
        JSONObject scoreObj2 = JSONUtil.parseObj(score2);

        scoreObj2.forEach((k, v) -> {
            if (!scoreObj1.containsKey(k)) {
                scoreObj1.set(k, v);
            } else {
                JSONObject s1 = scoreObj1.getJSONObject(k);
                JSONObject s2 = (JSONObject) v;
                s1.set("total", s1.getBigDecimal("total", BigDecimal.ZERO).add(
                        s2.getBigDecimal("total", BigDecimal.ZERO)));
                s1.set("scored", s1.getBigDecimal("scored", BigDecimal.ZERO).add(
                        s2.getBigDecimal("scored", BigDecimal.ZERO)));
            }
        });

        return scoreObj1;
    }

}
