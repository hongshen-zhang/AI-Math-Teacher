package com.chaty.dto;

import lombok.Data;

@Data
public class RemoteFileDTO {

    /**
     * 创建时间
     */
    private String created;
    /**
     * 是否是文件夹
     */
    private boolean isDir;
    /**
     * 文件名
     */
    private String name;
    /**
     * 大小
     */
    private long size;
    /**
     * 缩略图
     */
    private String thumb;
    /**
     * 类型
     */
    private long type;
    /**
     * 修改时间
     */
    private String modified;
    /**
     * 签名
     */
    private String sign;

}
