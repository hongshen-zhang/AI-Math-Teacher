package com.chaty.dto;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.map.MapUtil;
import lombok.Data;

@Data
public class DocReviewDTO {

    private String docname;
    private List<QuestionDTO> questions;
    private List<String> ids;
    private String model;
    private AreaDTO docIdArea;
    private int ocrType = 1;
    private int fontSize = 15;
    private int signSize = 40;
    private String docType = "A4";
    private String docurl;

    private String batchId;

    private Boolean isPreview = false;

    private Boolean isScore = false;
    private AreaDTO scoreArea;

    @Data
    public static class QuestionDTO {
        private String name;
        private String question;
        private String correctAnswer;
        private AreaDTO answerArea;
        private AreaDTO reviewArea;
        private AreaDTO checkArea;
        private String questionInfo;
        private String defaultReview;
        private Boolean isDefaultReview = false;
        private Float score = 0.0f;
    }

    @Data
    public static class AreaDTO {
        private int x;
        private int y;
        private int width;
        private int height;
    }

    public boolean isBatch() {
        return ids.size() > 1;
    }

    private final Map<Integer, String> ocrServices = MapUtil.builder(new HashMap<Integer, String>())
            .put(1, "mathPixOCRService")
            .put(2, "tencentOCRService")
            .put(3, "openaiService")
            .put(4, "paddleOCRService")
            .build();

    public String getPrimaryOcrService() {
        return ocrServices.get(ocrType);
    }

    public String getSecondaryOcrService() {
        return ocrServices.get((ocrType + 1) % ocrServices.size());
    }

}
