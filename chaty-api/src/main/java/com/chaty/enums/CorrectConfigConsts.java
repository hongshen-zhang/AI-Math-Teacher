package com.chaty.enums;

import cn.hutool.core.map.MapUtil;
import lombok.Data;
import java.util.Map;

public interface CorrectConfigConsts {

    @Data
    public static class ScoreFormat {
        private String pattern;
        private String delimiter = "+";
        private String lastDelimiter = "=";

        public ScoreFormat(String pattern) {
            this.pattern = pattern;
        }

        public ScoreFormat(String pattern, String delimiter, String lastDelimiter) {
            this.pattern = pattern;
            this.delimiter = delimiter;
            this.lastDelimiter = lastDelimiter;
        }
    }

    Map<Integer, ScoreFormat> SCORE_FORMAT_MAP = MapUtil
        .builder(1, new ScoreFormat("{0}:$\\mathit'{'{1}/{2}'}'$"))
        .put(2, new ScoreFormat("{0}:$\\mathtt'{'{1}/{2}'}'$"))
        .put(3, new ScoreFormat("{0}:$\\textbf'{'{1}/{2}'}'$"))
        .put(4, new ScoreFormat("{0}:$\\mathsf'{'{1}/{2}'}'$"))
        .put(5, new ScoreFormat("{0}:${1}/{2}$"))
        .put(6, new ScoreFormat("{1}", " ", " "))
        .put(7, new ScoreFormat("-{3}", " ", " "))
        .build();

    
}
