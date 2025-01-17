package com.chaty.util;

import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.ArgumentMatchers.matches;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class UtilTest {

    @Test
    public void test() {
        String str = "{\n\"isTrue\": false,\n\"review\": \"学生你好，你的答案有误。\\n(1) 在题目中已经给出 $2S_{n}=na_{n}$，其中$S_{n}$为$\\left\\{a_{n}\\right\\}$的前$n$项和，故$S_{n}=\\frac{na_{n}}{2}$。又因为$a_{2}=1$，所以$S_{2}=\\frac{2a_{2}}{2}=a_{2}=1$，由此求得通项公式为$a_{n}=n-1$。\\n(2) 对于数列$\\left\\{\\frac{a_{n}+1}{2^{n}}\\right\\}$的前 $n$ 项和 $T_{n}$，需要利用上一步求得的通项公式，再求和，正确的结果为$T_{n}=2-(2+n) \\cdot\\left(\\frac{1}{2}\\right)^{n}$。请再次尝试自我解答这道题目，相信你有能力得出正确的答案。\"\n}";
        System.out.println(str);
        String replaced = str.replaceAll("(?<!\\\\)\\\\(?!\\\\)", "\\\\\\\\")
                .replaceAll("\\\\\n", "\\\\n");
        System.out.println(replaced);
        JSONObject parsed = JSONUtil.parseObj(replaced);
        System.out.println(parsed.toStringPretty());
    }

    @Test
    public void test1() {
        String str = "### 是否正确\n1\n\n### 评语\n22222";
        // String str = "评语\n2";
        String regex = "(?<=(### 是否正确|### 评语)\\n)(.*)(?=(\\n\\n|))";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void test2() {
        String str = "[{\"type\":\"text\",\"text\":\"角色：阅卷老师\\n" + //
        "任务：判断学生作业的正确性，请严格遵守评卷指令，否则会被扣除评卷积分,\\n" + //
        "\\n" + //
        "指令：\\n" + //
        "1. 比较学生答案与标准答案：如果相同，则判定为正确（使用'1'表示）；否则判定为错误（使用'0'表示）。\\n" + //
        "2. 答案不可识别即为错误：若学生答案无法识别，则视为错误。\\n" + //
        "3. 不质疑标准答案：仅需对比学生答案和标准答案，不对标准答案的正确性提出疑问。\\n" + //
        "4. 严格遵循格式：按照指定格式输出评价结果，避免任何多余输出。\\n" + //
        "\\n" + //
        "输出格式：\\n" + //
        "- 是否正确：\\n" + 
        "{是否正确}\\n\\n" + //
        "- 描述学生答案：\\n" + 
        "{描述学生答案}\\n" + //
        "\\n" + //
        "相关信息：\\n" + //
        "- 题目：%s \\n" + //
        "- 标准答案：%s \"},{\"type\":\"image_url\",\"image_url\":{\"url\":\"%s\"}}]";
        
        System.out.println(JSONUtil.parseArray(str).toStringPretty());;
    }

}
