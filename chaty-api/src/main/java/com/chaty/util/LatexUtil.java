package com.chaty.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LatexUtil {
    
    // 转义LaTeX特殊字符
    public static String escapeLatex(String input) {
        if (Objects.isNull(input)) {
            return "";
        }
        
        // 独立处理反斜杠，因为它们必须首先转换
        // input = input.replaceAll("(?<!\\\\)\\\\(?!\\\\)", "\\\\\\\\");

        // 特殊字符的数组
        char[] specialChars = {'&', '%', '$', '#', '_', '^', '~'};
        // 对于每个特殊字符，构建正则表达式并替换
        for (char ch : specialChars) {
            String regex = "(?<!\\\\)" + Pattern.quote(String.valueOf(ch));
            String replacement = Matcher.quoteReplacement("\\" + ch);
            input = input.replaceAll(regex, replacement);
        }

        // 独立处理上标和下标字符
        input = input.replaceAll("(?<!\\\\)\\^(?!\\{)", "\\\\textasciicircum{}");
        input = input.replaceAll("(?<!\\\\)~(?!\\{)", "\\\\textasciitilde{}");

        return input;
    }

}
