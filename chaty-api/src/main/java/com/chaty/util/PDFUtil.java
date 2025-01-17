package com.chaty.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.chaty.exception.BaseException;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PDFUtil {

    private static int DPI = 300;

    public static void convert2Img(String pdfPath, String imgPath) {
        convert2Img(pdfPath, imgPath, null);
    }

    public static JSONObject convert2ImgWithImgInfo(String pdfPath, String imgPath) {
        return convert2ImgWithImgInfo(pdfPath, imgPath, null);
    }

    /**
     * Converts a PDF to an image.
     *
     * @param pdfPath the path to the PDF
     * @param imgPath the path to save the image
     */
    public static void convert2Img(String pdfPath, String imgPath, Integer pageIndex) {
        try {
            PDDocument document = PDDocument.load(new File(pdfPath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(Optional.ofNullable(pageIndex).orElse(0), DPI);
            ImageIO.write(bim, "JPEG", new File(imgPath));
            if (Objects.nonNull(pageIndex)) {
                // 将指定页数的PDF保存
                PDPage page = document.getPage(pageIndex);
                // 创建新的 PDF 文档
                PDDocument destinationDocument = new PDDocument();
                destinationDocument.addPage(page);
                destinationDocument.save(imgPath.replace(".jpg", ".pdf"));
                destinationDocument.close();
            }
            document.close();
        } catch (IOException e) {
            throw new BaseException("文档转图片失败!", e);
        }
    }

    /**
     * 返回图片的长宽
     * @param pdfPath
     * @param imgPath
     * @param pageIndex
     * @return
     */
    public static JSONObject convert2ImgWithImgInfo(String pdfPath, String imgPath, Integer pageIndex) {
        try {
            PDDocument document = PDDocument.load(new File(pdfPath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(Optional.ofNullable(pageIndex).orElse(0), DPI);
            ImageIO.write(bim, "JPEG", new File(imgPath));
            if (Objects.nonNull(pageIndex)) {
                // 将指定页数的PDF保存
                PDPage page = document.getPage(pageIndex);
                // 创建新的 PDF 文档
                PDDocument destinationDocument = new PDDocument();
                destinationDocument.addPage(page);
                destinationDocument.save(imgPath.replace(".jpg", ".pdf"));
                destinationDocument.close();
            }
            int width = bim.getWidth();
            int height = bim.getHeight();
            JSONObject obj = new JSONObject();
            obj.set("width", width);
            obj.set("height", height);
            obj.set("x", 0);
            obj.set("y", 0);
            document.close();
            return obj;
        } catch (IOException e) {
            throw new BaseException("文档转图片失败!", e);
        }
    }

    /**
     * 截取PDF文档的部分区域转为图片
     */
    public static void addTextToPDF(String filePath, String text, int x, int y, int width, int height) {
        try {
            PDDocument document = PDDocument.load(new File(filePath));
            PDPage page = document.getPage(0);
            PDPageContentStream contentStream = new PDPageContentStream(document, page,
                    PDPageContentStream.AppendMode.APPEND, true, true);

            // 获取page的宽度和高度
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            log.info("width: {}, height: {}", pageWidth, pageHeight);

            int fontSize = 12;
            PDType0Font font = PDType0Font.load(document, PDFUtil.class.getResourceAsStream("/font/STFANGSO.TTF"));
            String[] words = text.split("");
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(pixel2point(x) + 10, pageHeight - pixel2point(y) - 15);
            float currentWidth = 0;
            int line = 0;
            for (String word : words) {
                if ("\\n".equals(word) || "\n".equals(words)) {
                    currentWidth = 0;
                    line++;
                    contentStream.newLineAtOffset(0, -15);
                    continue;
                }
                float wordWidth = fontSize * font.getStringWidth(word) / 1000;
                if (currentWidth + wordWidth < pixel2point(width - 20)) {
                    contentStream.showText(word);
                    currentWidth += wordWidth;
                } else {
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText(word);
                    currentWidth = wordWidth;
                    line++;
                }
            }
            contentStream.endText();

            contentStream.setStrokingColor(Color.BLACK);
            contentStream.setLineWidth(1f);
            int recHeight = (line + 2) * 15;
            contentStream.addRect(pixel2point(x), pageHeight - pixel2point(y) - recHeight, pixel2point(width) + 20,
                    recHeight);
            contentStream.stroke();

            contentStream.close();
            document.save(filePath);
            document.close();
        } catch (IOException e) {
            throw new BaseException("文档添加文本失败", e);

        }
    }

    // private int addParagraph(PDPageContentStream contentStream, PDDocument
    // document, String text, int x, int y,
    // int width, int height) throws IOException {
    // String[] words = text.split("");
    // int line = 0;
    // int fontSize = 12;
    // PDType0Font font = PDType0Font.load(document,
    // PDFUtil.class.getResourceAsStream("/LXGWWenKaiScreenR.ttf"));

    // contentStream.beginText();
    // contentStream.setFont(font, fontSize);
    // contentStream.setLeading(TEXT_LEADING);

    // contentStream.newLineAtOffset(x + 10, y - 10);
    // float currentWidth = 0;
    // for (String word : words) {
    // if ("\n".equals(word)) {
    // currentWidth = 0;
    // line++;
    // contentStream.newLineAtOffset(0, -TEXT_LEADING);
    // }
    // float wordWidth = fontSize * font.getStringWidth(word) / 1000;
    // if (currentWidth + wordWidth < (width - 20)) {
    // contentStream.showText(word + "");
    // currentWidth += wordWidth;
    // } else {
    // contentStream.newLineAtOffset(0, -TEXT_LEADING);
    // contentStream.showText(word + " ");
    // currentWidth = wordWidth;
    // }
    // }

    // }

    /**
     * 截取指定PDF的区域保存为图片
     */
    public synchronized static void extractImageFromPDF(String filePath, String imagePath, int x, int y, int width,
            int height, List<JSONObject> markAreas) {
        try {
            PDDocument document = PDDocument.load(new File(filePath));
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, DPI); // Render the first page with 300 DPI
            extractImageFromImage(image, imagePath, x, y, width, height, markAreas);
            document.close();
        } catch (Exception e) {
            throw new BaseException("文档截取图片失败!", e);
        }
    }

    /**
     * 获取文档图片
     */
    public static BufferedImage getDocumentImage(String filePath) {
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(filePath));
            PDFRenderer renderer = new PDFRenderer(document);
            return renderer.renderImageWithDPI(0, DPI); // Render the first page with 300 DPI
        } catch (Exception e) {
            throw new BaseException("文档截取图片失败!", e);
        } finally {
            IoUtil.close(document);
        }
    }

    /**
     * 截取图片
     */
    public static void extractImageFromImage(BufferedImage image, String imagePath, int x, int y, int width,
            int height, List<JSONObject> markAreas) {
        try {
            int imageWidth = image.getWidth();
            if (x + width > imageWidth) {
                log.warn("图片截取超出文档, x: {}, width: {}, imageWidth: {}", x, width, imageWidth);
                width = imageWidth - x;
            }
            int imageHeight = image.getHeight();
            if (y + height > imageHeight) {
                log.warn("图片截取超出文档, y: {}, height: {}, imageHeight: {}", y, height, imageHeight);
                height = imageHeight - y;
            }

            BufferedImage subImage = image.getSubimage(x, y, width, height); // Extract the area

            // 添加区域标记
            if (Objects.nonNull(markAreas) && markAreas.size() > 0) {
                drawRectWithImage(subImage, markAreas);
            }

            ImageIO.write(subImage, "JPG", new File(imagePath)); // Save the image to a file
        } catch (Exception e) {
            throw new BaseException("文档截取图片失败!", e);
        }
    }

    public static void extractImageFromPDF(String filePath, String imagePath, int x, int y, int width,
            int height) {
        extractImageFromPDF(filePath, imagePath, x, y, width, height, null);
    }

    public static void drawRectWithImage(BufferedImage image, List<JSONObject> markAreas) {
        // 获取图片的 Graphics 对象
        Graphics g = image.getGraphics();

        // 设置颜色
        g.setColor(Color.GREEN);

        // 绘制矩形（x, y, width, height）
        markAreas.forEach(area -> {
            JSONObject markArea = (JSONObject) area;
            int x = markArea.getInt("x");
            int y = markArea.getInt("y");
            int width = markArea.getInt("width");
            int height = markArea.getInt("height");
            g.drawRect(x, y, width, height);
        });

        // 释放 Graphics 对象
        g.dispose();
    }

    private static float pixel2point(int pixel) {
        return pixel * 72f / DPI;
    }

    /**
     * 获取 PDF 页数
     * 
     * @param filePath
     * @return
     */
    public static int getPageNum(String filePath) {
        try {
            PDDocument document = PDDocument.load(new File(filePath));
            return document.getNumberOfPages();
        } catch (Exception e) {
            throw new BaseException("获取文档页数失败!", e);
        }
    }

    /**
     * 获取文档信息
     */
    public static JSONObject getPDFInfo(String filePath) {
        try {
            PDDocument document = PDDocument.load(new File(filePath));
            PDPage page = document.getPage(0);

            PDRectangle mediaBox =  page.getCropBox();
            int rotation = page.getRotation();
            // 高度和宽度转换为毫米
            float width = mediaBox.getWidth() * 25.4f / 72;
            float height = mediaBox.getHeight() * 25.4f / 72;

            // 考虑页面的旋转
            if (rotation == 90 || rotation == 270) {
                // 交换宽度和高度
                float temp = width;
                width = height;
                height = temp;
            }

            return new JSONObject()
                    .set("width", width)
                    .set("height", height);
        } catch (Exception e) {
            throw new BaseException("获取文档信息失败!", e);
        }
    }

}
