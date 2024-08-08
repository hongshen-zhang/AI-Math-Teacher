package com.chaty.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    Map<String, Object> saveFile(MultipartFile file);

}
