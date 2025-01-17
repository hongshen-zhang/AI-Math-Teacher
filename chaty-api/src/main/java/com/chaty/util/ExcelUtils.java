package com.chaty.util;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelReader;

import com.chaty.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class ExcelUtils {

    public static List<UserDTO> readExcel(MultipartFile file) throws IOException {
        ExcelReader reader = new ExcelReader(file.getInputStream(), 0);
        List<List<Object>> rows = reader.read();
        List<UserDTO> users = CollUtil.newArrayList();

        for (int i = 1; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            String username = row.get(0).toString();
            String studentId = row.get(1).toString();

            UserDTO user = new UserDTO();
            user.setUsername(username);
            user.setStudentId(studentId);
            users.add(user);
        }

        return users;
    }
}

