package com.chaty.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chaty.api.baiduai.QianFanApi;
import com.chaty.dto.ChatCompletionDTO;
import com.chaty.dto.MessageDTO;
import com.chaty.exception.BaseException;
import com.chaty.service.BasicAiService;

import cn.hutool.core.map.MapUtil;

@Service
public class BaiduAiServiceImpl implements BasicAiService {

    @Value("${api.baiduai.clientId}")
    private String clientId;
    @Value("${api.baiduai.clientSecret}")
    private String clientSecret;
    // token 过期时间 29天 官方30天
    @Value("${api.baiduai.authexpire:2505600}")
    private long authExpireSecs;

    private Long authExpireTimestamp;
    private String accessToken;

    private Object lock = new Object();

    @Resource
    private QianFanApi qianFanApi;

    private Map<String, Object> models = MapUtil
            .builder("ERNIE-Bot", null)
            .put("ERNIE-Bot-turbo", null)
            // .put("Embedding-V1", null)
            .put("BLOOMZ-7B", null)
            .build();

    @Override
    public Map<String, Object> chatForCompletion(ChatCompletionDTO param) {
        String accessToken = getAccessToken();
        String model = param.getModel();

        Map<String, Object> res = null;

        if (Objects.equals(model, "ERNIE-Bot")) {
            res = chatWithErnie(accessToken, param, false);
        }
        if (Objects.equals(model, "ERNIE-Bot-turbo")) {
            res = chatWithErnie(accessToken, param, true);
        }
        if (Objects.equals(model, "Embedding-V1")) {
            res = chatWithEbV1(accessToken, param);
        }
        if (Objects.equals(model, "BLOOMZ-7B")) {
            res = chatWithBloomz7b1(accessToken, param);
        }

        return res;
    }

    public String getAccessToken() {
        long current = System.currentTimeMillis();
        if (Objects.isNull(authExpireTimestamp) || current > authExpireTimestamp) {
            synchronized (lock) {
                if (Objects.isNull(authExpireTimestamp) || current > authExpireTimestamp) {
                    Map<String, Object> res = qianFanApi.getAccessToken(clientId, clientSecret);
                    if (res.containsKey("error")) {
                        throw new RuntimeException(
                                String.format("API授权失败: %s", MapUtil.getStr(res, "error_description")));
                    }
                    this.accessToken = MapUtil.getStr(res, "access_token");
                    this.authExpireTimestamp = current + (authExpireSecs * 1000);
                }
            }
        }
        return accessToken;
    }

    @Override
    public Boolean isSupport(String model) {
        return models.containsKey(model);
    }

    private Map<String, Object> chatWithErnie(String accessToken, ChatCompletionDTO param, boolean isTurbo) {
        Map<String, Object> apiParam = new HashMap<String, Object>();
        List<Map<String, ?>> messages = param.getMessages().stream().map(m -> {
            return MapUtil.builder("role", m.getRole()).put("content", m.getContent()).build();
        }).collect(Collectors.toList());
        apiParam.put("messages", messages);
        if (Objects.nonNull(param.getTemperature())) {
            apiParam.put("temperature", param.getTemperature());
        }
        if (Objects.nonNull(param.getTopp())) {
            apiParam.put("top_p", param.getTopp());
        }
        if (Objects.nonNull(param.getPenaltyScore())) {
            apiParam.put("penalty_score", param.getPenaltyScore());
        }
        apiParam.put("stream", Optional.ofNullable(param.getStream()).orElse(false));
        if (Objects.nonNull(param.getUserid())) {
            apiParam.put("user_id", param.getUserid());
        }

        Map<String, Object> resp = null;
        if (isTurbo) {
            resp = qianFanApi.chatWithErniePro(accessToken, apiParam);
        } else {
            resp = qianFanApi.chatWithErnie(accessToken, apiParam);
        }
        afterResponse(resp);

        resp.put("$response", resp.get("result"));

        return resp;
    }

    /**
     * 这玩意不能用
     */
    @Deprecated
    private Map<String, Object> chatWithEbV1(String accessToken, ChatCompletionDTO param) {
        Map<String, Object> apiParam = new HashMap<String, Object>();
        List<?> input = param.getMessages().stream().map(MessageDTO::getContent).collect(Collectors.toList());
        apiParam.put("input", input);
        if (Objects.nonNull(param.getUserid())) {
            apiParam.put("user_id", param.getUserid());
        }

        Map<String, ?> resp = qianFanApi.chatWithEbV1(accessToken, apiParam);
        afterResponse(resp);

        return null;
    }

    private Map<String, Object> chatWithBloomz7b1(String accessToken, ChatCompletionDTO param) {
        Map<String, Object> apiParam = new HashMap<String, Object>();
        List<Map<String, ?>> messages = param.getMessages().stream().map(m -> {
            return MapUtil.builder("role", m.getRole()).put("content", m.getContent()).build();
        }).collect(Collectors.toList());
        apiParam.put("messages", messages);
        apiParam.put("stream", Optional.ofNullable(param.getStream()).orElse(false));
        if (Objects.nonNull(param.getUserid())) {
            apiParam.put("user_id", param.getUserid());
        }

        Map<String, Object> resp = qianFanApi.chatWithBloomz7b1(accessToken, apiParam);
        afterResponse(resp);

        resp.put("$response", resp.get("result"));

        return resp;
    }

    private void afterResponse(Map<String, ?> resp) {
        if (resp.containsKey("error_code")) {
            throw new BaseException(
                    String.format("发起对话失败: %s", MapUtil.getStr(resp, "error_msg")));
        }
    }

}
