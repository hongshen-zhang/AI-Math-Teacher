package com.chaty.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chaty.exception.BaseException;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RuntimeUtil;

public interface PDFService {

    static Logger log = LoggerFactory.getLogger(PDFService.class);

    Map<String, Object> createPDF(Map<String, Object> params);

    Map<String, Object> solveAllPDF(Map<String, Object> params);

    Map<String, Object> coursenotePDF(Map<String, Object> params);

    default String writePDF(String texContent, Map<String, Object> properties) {
        String filePath = MapUtil.getStr(properties, "filePath");
        String texPath = MapUtil.getStr(properties, "texPath");
        String fileCtxPath = MapUtil.getStr(properties, "fileCtxPath");

        try {
            String filename = IdUtil.fastSimpleUUID();
            String texFilePath = String.format("%s/%s.tex", texPath, filename);
            File execEnv = FileUtil.file(filePath);
            FileUtil.writeBytes(texContent.getBytes(StandardCharsets.UTF_8), texFilePath);
            Process process = RuntimeUtil.exec(null, execEnv, "xelatex", "-interaction=nonstopmode", texFilePath);
            String pdfres = RuntimeUtil.getResult(process);
            log.debug("PDF生成日志：{}", pdfres);
            return String.format("%s/%s.pdf", fileCtxPath, filename);
        } catch (Exception e) {
            throw new BaseException("PDF文件生成失败!", e);
        }
    }

}
