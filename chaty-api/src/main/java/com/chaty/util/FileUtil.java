package com.chaty.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtil {

    @Value("${file.local.path}")
    public String path;
    @Value("${file.local.ctxpath}")
    public String ctxPath;
    @Value("${server.url}")
    public String serverUrl;

    public static FileUtil INSTANCE = null;

    @PostConstruct
    public void postConstrect() {
        FileUtil.INSTANCE = this;
    }

    /**
     * eg: http://ip:port/static/1.jpg -> /tmp/1.jpg
     */
    public String url2Path(String url) {
        return url.replace(serverUrl + ctxPath, path);
    }

    /**
     * eg: /static/1.jpg -> /tmp/1.jpg
     */
    public String ctxUrl2Path(String url) {
        return url.replace(ctxPath, path);
    }

    public String relUrl2Path(String url) {
        return url.replace(ctxPath, path);
    }

    public String url2Base64(String url) {
         BufferedImage image = null;
        if (url.startsWith(serverUrl)) {
            // 本地文件
            String filePath = url2Path(url);
            image = ImgUtil.read(filePath);
        } else {
            image = ImgUtil.read(URLUtil.url(url));
        }
        return ImgUtil.toBase64(image, "jpg");
    }

    public String docAreaImg(String docPath, int x, int y, int width, int height) {
        return docAreaImg(docPath, x, y, width, height, null);
    }

    public String docAreaImg(String docPath, int x, int y, int width, int height, List<JSONObject> markAreas) {
        String absDocPath = String.format("%s/%s", path, docPath);
        String imgName = IdUtil.fastSimpleUUID() + ".jpg";
        String imgPath = String.format("%s/%s", path, imgName);
        PDFUtil.extractImageFromPDF(absDocPath, imgPath, x, y, width, height, markAreas);

        ThreadUtil.sleep(1000); // 等待图片生成

        return imgName;
    }

    /**
     * 多区域图片直接生成
     */
    public JSONArray setDocAreasImg(String docPath, JSONArray areas) {
        String absDocPath = String.format("%s/%s", path, docPath);
        BufferedImage image = PDFUtil.getDocumentImage(absDocPath);
        JSONArray res = new JSONArray();
        for (int i = 0; i < areas.size(); i++) {
            JSONObject areaObj = areas.getJSONObject(i);
            JSONObject area = areaObj.getJSONObject("area");
            if (Objects.nonNull(area)) {
                String imgName = IdUtil.fastSimpleUUID() + ".jpg";
                String imagePath = String.format("%s/%s", path, imgName);
                int x = area.getInt("x");
                int y = area.getInt("y");
                int width = area.getInt("width");
                int height = area.getInt("height");
                PDFUtil.extractImageFromImage(image, imagePath, x, y, width, height, getQsAreas(areaObj));
                // 设置图片路径
                areaObj.set("areaImg", String.format("%s/%s", ctxPath, imgName));
            }
            res.add(areaObj);
        }
        ThreadUtil.sleep(1000); // 睡一下，防止程序读到未保存的图片
        return res;
    }

    /**
     * 获取题目对应的区域
     */
    public List<JSONObject> getQsAreas(JSONObject areaObj) {
        JSONObject area = areaObj.getJSONObject("area");
        return areaObj.getJSONArray("questions").stream()
                .map(qs -> {
                    JSONObject qsPosArea = ((JSONObject) qs).getJSONObject("qsPosArea");
                    if (Objects.nonNull(qsPosArea)) {
                        qsPosArea.set("x", qsPosArea.getInt("x") - area.getInt("x"));
                        qsPosArea.set("y", qsPosArea.getInt("y") - area.getInt("y"));
                    }
                    return qsPosArea;
                })
                .filter(a -> Objects.nonNull(a))
                .collect(Collectors.toList());
    }

    public String absPath(String relPath) {
        return String.format("%s/%s", path, relPath);
    }

    public String docUrl2ImgUrl(String docPath) {
        String filename = docPath.substring(docPath.lastIndexOf("/") + 1).split(("\\."))[0];
        String imgName = String.format("%s.%s", filename, "jpg");
        PDFUtil.convert2Img(relUrl2Path(docPath), String.format("%s/%s", path, imgName));
        return String.format("%s/%s", ctxPath, imgName);
    }

    String[] ZIP_FILE_SUFFIX = {"不含原卷", "含原卷", "统计结果"};

    public String zipUrls(String name, List<String> docUrls) {
        // 压缩文件
        String zipName = String.format("%s.zip", name);
        String zipPath = String.format("%s/%s", path, zipName);
        String srcPath = String.format("%s/%s", path, IdUtil.fastSimpleUUID());
        for (int i = 0; i < docUrls.size(); i++) {
            String url = docUrls.get(i);
            String path = relUrl2Path(url);
            cn.hutool.core.io.FileUtil.copy(path, String.format("%s/%s(%s).pdf", srcPath, name, ZIP_FILE_SUFFIX[i]),
                    true);
        }
        ZipUtil.zip(srcPath, zipPath, false);
        return String.format("%s/%s", ctxPath, zipName);
    }

    /**
     * 预加载文件
     * 
     * @param url eg: /static/1.pdf
     */
    public void preLoadFile(String url) {
        log.info("预加载文件: url-{}", url);
        // 获取文件名
        String path = ctxUrl2Path(url);
        // 判断文件是否存在
        log.info("预加载文件: path-{}", path);
        File file = cn.hutool.core.io.FileUtil.file(path);
        if (file.exists()) {
            // 如果文件存在，直接返回
            return;
        }
        // 请求服务器下载文件
        String downloadUrl = String.format("%s%s", serverUrl, url);
        log.info("预加载文件: downloadUrl-{}", downloadUrl);
        // 下载文件
        HttpUtil.downloadFile(downloadUrl, file);
    }

}
