package com.chaty.util;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.CHAIN_APPROX_SIMPLE;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.Canny;
import static org.bytedeco.opencv.global.opencv_imgproc.RETR_TREE;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.findContours;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.junit.jupiter.api.Test;

import cn.hutool.json.JSONUtil;

public class JavacvUtil {

    @Test
    public void findRectanglesInPdf() {
        Mat src = imread("D:\\tmp\\1.jpg");
        Mat gray = new Mat();
        cvtColor(src, gray, COLOR_BGR2GRAY);

        Mat edges = new Mat();
        Canny(gray, edges, 100, 200, 3, false);

        MatVector matVector = new MatVector();
        Mat hierarchy = new Mat();
        findContours(edges, matVector, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);

        List<Rect> rects = new ArrayList<>();

        for (int i = 0; i < matVector.size(); i++) {
            Mat contour = matVector.get(i);
            // 近似轮廓
            double epsilon = 0.05 * opencv_imgproc.arcLength(new Mat(contour), true);
            Mat approx = new Mat();
            opencv_imgproc.approxPolyDP(new Mat(contour), approx, epsilon, true);

            // 检查近似轮廓是否有4个顶点（可能是长方形）
            if (approx.rows() == 4) {
                // 计算轮廓的边界框
                Rect rect = opencv_imgproc.boundingRect(new Mat(approx));
                if (rect.width() * rect.height() > 400 && 0.1 < (float) rect.width() / rect.height()
                        && (float) rect.width() / rect.height() < 2) {
                    // 检查新的矩形是否接近已存在的矩形
                    boolean closeToExisting = rects.stream()
                            .anyMatch(existing -> Math.abs(rect.y() - existing.y()) <= 10);
                    if (!closeToExisting) {
                        rects.add(rect);
                    }
                }
            }
        }

        for (Rect rect : rects) {
            System.out.println(String.format("x: %s, y: %s, w: %s, h: %s",
                    rect.x(), rect.y(), rect.width(), rect.height()));
        }
    }

    @Test
    public void test1() {
        List<Map<String, Integer>> points = CVUtil.findRectInImg("D:\\tmp\\1.jpg");
        System.out.println(JSONUtil.toJsonPrettyStr(points));
    }

}
