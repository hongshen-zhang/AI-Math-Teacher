package com.chaty.service.impl;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.chaty.exception.BaseException;
import com.chaty.service.OCRService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.GeneralAccurateOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.GeneralAccurateOCRResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Primary
@Service("tencentOCRService")
public class TencentOCRServiceImpl implements OCRService {

    @Value("${api.temcentcloud.endpoint:ocr.tencentcloudapi.com}")
    public String endpoint;
    @Value("${api.temcentcloud.secretid}")
    public String secretid;
    @Value("${api.temcentcloud.secretkey}")
    public String secretKey;
    @Value("${api.temcentcloud.region:ap-shanghai}")
    public String region;

    private HttpProfile httpProfile;

    @PostConstruct
    public void postConstruct() {
        log.info("tencent cloud api configuration: \n endpoint: {} \n secretid: {} \n secretKey: {} \n region: {} \n",
                endpoint, secretid, secretKey, region);
    }

    private OcrClient createOcrClient() {
        Credential cred = new Credential(secretid, secretKey);
        httpProfile = new HttpProfile();
        httpProfile.setEndpoint(endpoint);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        return new OcrClient(cred, region, clientProfile);
    }

    @Override
    public String ocrForText(String url) {
        try {
            OcrClient client = createOcrClient();
            // 通用印刷体识别（高精度版）
            GeneralAccurateOCRRequest req = new GeneralAccurateOCRRequest();
            req.setImageUrl(url);
            GeneralAccurateOCRResponse resp = client.GeneralAccurateOCR(req);
            return Arrays.stream(resp.getTextDetections()).map(d -> d.getDetectedText()).reduce("", (a, b) -> a + b);
        } catch (TencentCloudSDKException e) {
            log.error("tencent cloud ocr api error: {}", e.getMessage());
            throw new BaseException("OCR 识别失败", e);
        }
    }

}