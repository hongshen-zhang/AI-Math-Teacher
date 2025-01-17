package com.chaty.completion;

import cn.hutool.json.JSONObject;

public interface AnswerCardReviewService {
    
    public JSONObject reviewAnswerCard(String filePath, JSONObject options);

}
