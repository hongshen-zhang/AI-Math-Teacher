package com.chaty.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chaty.dto.ChatCompletionDTO;
import com.chaty.exception.BaseException;
import com.chaty.service.BasicAiService;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CPMAIServiceImpl implements BasicAiService {

    @Value("${api.aws.sk}")
    private String sk;
    @Value("${api.aws.ak}")
    private String ak;
    @Value("${api.aws.endpoint}")
    private String endpoint;
    @Value("${api.aws.host}")
    private String host;

    @Override
    public Map<String, Object> chatForCompletion(ChatCompletionDTO param) {
        Map<String, Object> requestParam = new HashMap<String, Object>();
        param.getMessages().forEach(msg -> {
            if (Objects.equals("user", msg.getRole())) {
                if (!requestParam.containsKey("input")) {
                    requestParam.put("input", msg.getContent());
                } else {
                    if (requestParam.containsKey("question")) {
                        requestParam.put("question", requestParam.get("question") + "\n" + msg.getContent());
                    } else {
                        requestParam.put("question", msg.getContent());
                    }
                }
            }
        });
        // requestParam.put("options", Collections.singletonList("\"<option_0>\":
        // \"严格按照我指定的格式回复我，不要回复多余的信息\""));
        requestParam.put("<ans>", "");
        requestParam.put("promot", "问答, 按照我指定的格式回复我");
        String requestStr = JSONUtil.toJsonStr(requestParam);

        log.info("请求 CPM hackthon 模型: {}", requestStr);
        String res = requestForStr(requestStr);
        log.info("请求 CPM hackthon 模型结果: {}", res);

        JSONObject resObj = null;
        if (Objects.nonNull(res)) {
            resObj = JSONUtil.parseObj(res);
            resObj.set("$response", resObj.getStr("<ans>"));
        }

        return Optional.ofNullable(resObj)
                .map(o -> JSONUtil.toBean(o, HashMap.class))
                .orElseGet(() -> new HashMap<String, Object>());
    }

    private String requestForStr(String inputJson) {
        try {
            long timestamp = Instant.now().getEpochSecond();
            String sign = md5Hash(Long.toString(timestamp) + sk);

            Map<String, Object> payload = new HashMap<>();
            payload.put("endpoint_name", endpoint);
            payload.put("input", inputJson);
            payload.put("ak", ak);
            payload.put("timestamp", Long.toString(timestamp));
            payload.put("sign", sign);

            HttpResponse resp = HttpUtil.createPost(String.format("%s/inference", host))
                    .contentType("application/json;charset=UTF-8")
                    .body(JSONUtil.toJsonStr(payload))
                    .execute();
            if (!resp.isOk()) {
                throw new BaseException(String.format("请求模型失败: %s", resp.body()));
            }

            String respStr = resp.body();
            return JSONUtil.parseObj(respStr).getByPath("data.data", String.class);
        } catch (Exception e) {
            throw new BaseException("请求模型失败", e);
        }
    }

    private static String md5Hash(String original) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(original.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    public Boolean isSupport(String model) {
        return "CPM hackthon".equals(model);
    }

}
