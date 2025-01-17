package com.chaty.util;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.CHAIN_APPROX_SIMPLE;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.Canny;
import static org.bytedeco.opencv.global.opencv_imgproc.RETR_TREE;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.findContours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;

public class CVUtil {

    private final static int RECT_X = 315;

    public static List<Map<String, Integer>> findRectInImg(String imgfile) {
        Mat src = imread(imgfile);
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

        List<Map<String, Integer>> points = new ArrayList<>();
        for (Rect rect : rects) {
            int x = rect.x();
            int y = rect.y();
            // 取10像素的误差...
            if (Math.abs(x - RECT_X) < 100) {
                Map<String, Integer> point = new HashMap<>();
                point.put("x", x);
                point.put("y", y);
                points.add(point);
            }
        }

        return points.stream().sorted((p1, p2) -> {
            return p1.get("y") - p2.get("y");
        }).collect(Collectors.toList());
    }

}
