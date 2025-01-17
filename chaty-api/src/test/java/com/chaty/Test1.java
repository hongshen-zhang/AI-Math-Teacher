package com.chaty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chaty.api.essayScoring.EssayScoreApi;
import com.chaty.api.essayScoring.EssayScoreBaseResponse;
import org.junit.jupiter.api.Test;

import com.chaty.util.PDFUtil;

import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class Test1 {
    @Resource
    private EssayScoreApi essayScoreApi;

    @Test
    public void test1() {
        JSONObject text = new JSONObject();
        text.set("text", "As the twins around them in disappointment, their father appeared. Their father knew what they were up to and didn't scold them. Then their father told them to clean up the kitchen and helped them to make some egg sandwiches and woked some porridge. The twins cheered up. They did as told and prepared the breakfast tray and add a card on the tray.\\n\\nThe twins carried the breakfast upstairs and woke their mother up. Then the twins kissed her and shouted \\\"happy mother's day\\\". Their mother eyes widened at the sight of the breakfast. She bit into a sandwich. To her surprise that the sandwich was tasted so delicious. She commended twins & it was most delicious breakfast I have ever eaten. Then the living bedroom laughter linger on ground the bedroom.");
        EssayScoreBaseResponse response = essayScoreApi.getScore(JSONUtil.toJsonStr(text));
        System.out.println("转换后的分数" + response.getScore2TotalScore(100F));
    }

    @Test
    public void jsonTest() {
        String str = "{\"isTrue\": false,\"review\": \"学生的计算错误了，斜边长度计算公式应该是\\(c = \\sqrt{a^2 + b^2}\\)，而不是\\(a^2 + b^2 = c\\)。此外，学生在最后一句话中表达得不清楚，可能有打字错误。\"}";
        System.out.println(EscapeUtil.escape(str));
        JSONObject obj = JSONUtil.parseObj(EscapeUtil.escape(str), false);
        System.out.println(obj);
    }

    @Test
    public void jsonTest1() {
        JSONObject obj = new JSONObject();
        obj.set("isTrue", false);
        obj.set("review",
                "学生的计算错误了，斜边长度计算公式应该是\\(c = \\sqrt{a^2 + b^2}\\)，而不是\\(a^2 + b^2 = c\\)。此外，学生在最后一句话中表达得不清楚，可能有打字错误。");
        System.out.println(obj);
    }

    @Test
    public void test2() {
        String matched = ReUtil.getGroup0("[0-9]+", "\\( 2 \\)");
        System.out.println(matched);
    }

    @Test
    public void test3() {
        String regex = "(?<=-(\\s?)学生答案\\n)([\\d|\\D]*?)(?=\\n-)";
        String str = "```\n" +
                "---\n" +
                "- 学生答案\n" +
                "0.43\n" +
                "0.55\n" +
                "- 是否正确\n" +
                "Y\n" +
                "- 批改意见\n" +
                "学生回答正确\n" +
                "---\n" +
                "- 学生答案\n" +
                "0.1\n" +
                "- 是否正确\n" +
                "Y\n" +
                "- 批改意见\n" +
                "学生回答正确\n" +
                "---\n" +
                "- 学生答案\n" +
                "7.0\n" +
                "- 是否正确\n" +
                "Y\n" +
                "- 批改意见\n" +
                "学生回答正确\n" +
                "---\n" +
                "- 学生答案\n" +
                "1\n" +
                "- 是否正确\n" +
                "Y\n" +
                "- 批改意见\n" +
                "学生回答正确\n" +
                "---\n" +
                "- 学生答案\n" +
                "0.62\n" +
                "- 是否正确\n" +
                "Y\n" +
                "- 批改意见\n" +
                "学生回答正确\n" +
                "---\n" +
                "- 学生答案\n" +
                "0.66\n" +
                "- 是否正确\n" +
                "Y\n" +
                "- 批改意见\n" +
                "学生回答正确\n" +
                "---\n" +
                "```";
        System.out.println(ReUtil.findAll(regex, str, 0));
    }

    @Test
    public void test4() {
        String filePath = "/home/ubuntu/workspace/chaty/chaty-api/src/test/resource/0bd18a0257114949acbd4dedaefb7d09.pdf";
        JSONObject pdfInfo = PDFUtil.getPDFInfo(filePath);
        System.out.println(pdfInfo.toStringPretty());
    }

}
