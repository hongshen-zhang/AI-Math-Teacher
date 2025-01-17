package com.chaty.dto;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class EssayWordDTO {

    /**
     * 结构分析
     */
    private String structuralAnalysis;

    /**
     * 写作建议
     */
    private String writingTips;

    /**
     * 作文提升
     */
    private String essayImprovement;

    /**
     * 高级词汇
     */
    private String advancedVocabulary;

    /**
     * 亮点表达
     */
    private String advancedExpression;

    /**
     * 拼写错误
     */
    private String spellingErrors;

    /**
     * 语法错误
     */
    private String syntaxError;

    /**
     * 用词不当
     */
    private String improperUseOfWords;

    /**
     * 成绩
     */
    private String grade;

    /**
     * 姓名
     */
    private String name;

    /**
     * 作文全文
     */
    private String fullText;

    /**
     * 总分数
     */
    private String allScore;

    /**
     * 重写后的分数
     */
    private String improvementGrade;

    /**
     * 学生姓名
     */
    private String studentName;

    private String essayTitle;

    private String gradeName;


    public void initByRecord(DocCorrectRecordDTO recordDTO, Float score, String gradeName, String essayTitle) {
        this.name = recordDTO.getDocname();
        this.allScore = String.valueOf(score);
        this.gradeName = gradeName;
        this.essayTitle = essayTitle;
        JSONArray reviewedObj = recordDTO.getReviewedObj();
        if (reviewedObj.isEmpty()) {
            return;
        }
        JSONObject reviewItem0 = reviewedObj.getJSONObject(0);
        JSONArray reviewed = reviewItem0.getJSONArray("reviewed");
        if (reviewed.isEmpty()) {
            return;
        }
        JSONObject reviewedItem0 = reviewed.getJSONObject(0);
        if (!reviewedItem0.containsKey("review")) {
            // 批改失败
            return;
        }
        JSONObject review = reviewedItem0.getJSONObject("review");
        this.grade = reviewedItem0.getJSONObject("0").getStr("scored");
        this.fullText = review.getStr("学生作文全文");
        this.structuralAnalysis = review.getStr("结构分析");
        this.spellingErrors = review.getJSONObject("错误分析").getStr("拼写错误");
        this.syntaxError = review.getJSONObject("错误分析").getStr("语法错误");
        this.improperUseOfWords = review.getJSONObject("错误分析").getStr("用词不当");
//        this.advancedVocabulary = review.getJSONObject("亮点分析").getStr("高级词汇");
//        this.advancedExpression = review.getJSONObject("亮点分析").getStr("亮点表达");
        this.writingTips = review.getStr("写作建议");
        this.essayImprovement = review.getStr("作文重写");
        this.improvementGrade = review.getStr("重写后的分数");
        this.studentName = review.getStr("学生姓名");

        this.essayImprovement = this.essayImprovement.replaceAll("\n\n", "\n");
        this.fullText = this.fullText.replaceAll("\n\n", "\n");
        this.spellingErrors = this.spellingErrors.replaceAll("\n", "");
        this.syntaxError = this.syntaxError.replaceAll("\n", "");
        this.improperUseOfWords = this.improperUseOfWords.replaceAll("\n", "");
        this.studentName = this.studentName.replaceAll("\n", "");

//        this.advancedVocabulary = this.advancedVocabulary.replaceAll("\n", "");
//        this.advancedExpression = this.advancedExpression.replaceAll("\n", "");
    }
}
