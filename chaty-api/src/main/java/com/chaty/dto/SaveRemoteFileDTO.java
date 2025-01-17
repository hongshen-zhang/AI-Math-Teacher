package com.chaty.dto;

import lombok.Data;

@Data
public class SaveRemoteFileDTO {

    private boolean isSave;

    /**
     * 保存文件路径
     */
    private String path;

    /**
     * 保存到远程文件夹的文件名
     */
    private String saveFileName;

    /**
     * 本地文件名
     */
    private String filename;

}
