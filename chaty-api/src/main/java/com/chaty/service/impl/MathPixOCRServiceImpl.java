package com.chaty.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chaty.api.mathpix.MathPixApi;
import com.chaty.exception.BaseException;
import com.chaty.service.OCRService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("mathPixOCRService")
public class MathPixOCRServiceImpl implements OCRService {

    @Value("${api.mathpix.app-id}")
    private String appid;
    @Value("${api.mathpix.app-key}")
    private String appkey;

    @Resource
    private MathPixApi mathPixApi;

    @Override
    public String ocrForText(String url) {
        Map<String, Object> params = createParams(url);
        Map<String, Object> response = mathPixApi.text(appid, appkey, params);
        
        verifyResponse(response);

        return String.valueOf(response.get("latex_styled"));
    }

    private Map<String, Object> createParams(String url) {
        Map<String, Object> params = new HashMap<>();
        params.put("src", url);
        params.put("formats", Arrays.asList("text", "latex_styled"));
        return params;
    }

    private void verifyResponse(Map<String, Object> response) {
        if (response.containsKey("error_info")) {
            Map<String, Object> errorInfo = (Map<String, Object>) response.get("error_info");
            String message = (String) errorInfo.get("message");
            String code = (String) errorInfo.get("code");
            log.error("图片识别失败: {}", JSONUtil.toJsonStr(response));
            throw new BaseException(String.format("图片识别失败: %s", message));
        }
    }
}
