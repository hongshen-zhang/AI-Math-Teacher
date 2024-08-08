package com.chaty.service.impl;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chaty.exception.BaseException;
import com.chaty.service.FileService;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;

@Service
public class LocalFileServiceImpl implements FileService {

    @Value("${file.local.path}")
    public String path;
    @Value("${file.local.ctxpath}")
    public String ctxPath;

    @Override
    public Map<String, Object> saveFile(MultipartFile file) {
        try {
            String extName = FileUtil.extName(file.getOriginalFilename());
            String filename = String.format("%s.%s", IdUtil.fastSimpleUUID(), extName);
            //
            FileUtil.writeFromStream(file.getInputStream(), String.format("%s/%s", path, filename));
            //
            return Collections.singletonMap("url", String.format("%s/%s", ctxPath, filename));
        } catch (Exception e) {
            throw new BaseException("文件上传失败!", e);
        }
    }

}
