package com.chaty.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.chaty.exception.BaseException;
import com.chaty.service.PDFService;

import cn.hutool.core.map.MapUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PDFServiceImpl implements PDFService {

    @Value("${file.tex.path}")
    private String texPath;
    @Value("${file.local.ctxpath}")
    private String fileCtxPath;
    @Value("${file.local.path}")
    private String filePath;

    private Map<String, Object> properties;

    @Resource
    private Configuration freemakerConfig;

    @PostConstruct
    public void init() {
        properties = new HashMap<String, Object>();
        properties.put("texPath", texPath);
        properties.put("fileCtxPath", fileCtxPath);
        properties.put("filePath", filePath);
    }

    private Map<String, String> REVIEW_VERS = MapUtil
            .builder("v1", "review.tex")
            .put("v2", "review_v2.tex")
            .build();

    @Override
    public Map<String, Object> createPDF(Map<String, Object> params) {
        try {
            String templateName = Optional.ofNullable(params.get("version")).map(v -> REVIEW_VERS.get(v)).orElse("review_2.tex");
            Template template = freemakerConfig.getTemplate(templateName);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
            log.debug("review content: {}", content);
            String fileUrl = writePDF(content, properties);
            return Collections.singletonMap("fileUrl", fileUrl);
        } catch (Exception e) {
            log.error("生成PDF失败", e);
            throw new BaseException("PDF文件生成失败!", e);
        }
    }

    private Map<String, String> SOLVEALL_VERS = MapUtil
            .builder("v1", "solveall.tex")
            .put("v2", "solveall_v2.tex")
            .build();

    @Override
    public Map<String, Object> solveAllPDF(Map<String, Object> params) {
        try {
            // params.put("accuracy", RandomUtil.randomInt(90, 100));
            String templateName = Optional.ofNullable(params.get("version")).map(v -> SOLVEALL_VERS.get(v)).orElse("solveall_v2.tex");
            Template template = freemakerConfig.getTemplate(templateName);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
            log.debug("solveall content: {}", content);
            String fileUrl = writePDF(content, properties);
            return Collections.singletonMap("fileUrl", fileUrl);
        } catch (Exception e) {
            log.error("生成PDF失败", e);
            throw new BaseException("生成PDF失败!", e);
        }
    }

    private Map<String, String> COURSENOTE_VERS = MapUtil
            .builder("v1", "coursenote.tex")
            .put("v2", "coursenote_v2.tex")
            .build();

    @Override
    public Map<String, Object> coursenotePDF(Map<String, Object> params) {
        try {
            String templateName = Optional.ofNullable(params.get("version")).map(v -> COURSENOTE_VERS.get(v)).orElse("coursenote_v2.tex");
            Template template = freemakerConfig.getTemplate(templateName);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
            log.debug("coursenote content: {}", content);
            String fileUrl = writePDF(content, properties);
            return Collections.singletonMap("fileUrl", fileUrl);
        } catch (Exception e) {
            log.error("生成PDF失败", e);
            throw new BaseException("生成PDF失败!", e);
        }
    }

}
