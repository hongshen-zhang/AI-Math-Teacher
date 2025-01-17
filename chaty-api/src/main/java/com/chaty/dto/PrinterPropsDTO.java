package com.chaty.dto;

import com.chaty.api.lianke.LiankeConfig;
import lombok.Data;

import java.util.List;

@Data
public class PrinterPropsDTO {

    /**
     * 是否打印
     */
    private boolean isPrint;

    private LiankeConfig.Device device;

    /**
     * 打印文件地址
     * 和下面的filename二选一
     */
    private String url;

    /**
     * 打印文件名称
     * 和上面的url二选一
     */
    private String filename;

    /**
     * 打印纸张尺寸,
     * 9：A4
     * 11：A5
     */
    private Integer dmPaperSize;

    /**
     * 打印纸张方向
     * 1：竖向
     * 2：横向
     */
    private Integer dmOrientation;

    /**
     * 打印份数
     */
    private Integer dmCopies;

    /**
     * 打印颜色
     * 1：黑白
     * 2：彩色
     */
    private Integer dmColor;

    /**
     * 双面打印
     * 1：关闭
     * 2：长边
     * 3：短边
     */
    private Integer dmDuplex;

    /**
     * 自动旋转功能，建议打印文档时才打开，可以解决部分文档横竖向页面混搭；0关闭，1开启
     */
    private Integer jpAutoRotate;

    /**
     * 文档逆序功能，仅对文档类型文件有效，图片无效；0关闭，1开启
     */
    private Integer pdfRev;

    /**
     * 纸张来源
     */
    private Integer dmDefaultSource;

    /**
     * 纸张类型
     */
    private Integer dmMediaType;
}
