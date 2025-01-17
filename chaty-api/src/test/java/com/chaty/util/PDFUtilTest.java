package com.chaty.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Test;

public class PDFUtilTest {

    @Test
    public void convert2ImgTest() {
        String pdfPath = "D:\\tmp\\1.pdf";
        String imgPath = "D:\\tmp\\2.jpg";
        PDFUtil.convert2Img(pdfPath, imgPath);
    }

    @Test
    public void addTextToPDFTest() {
        String pdfPath = "D:\\tmp\\CCF_000020.pdf";
        String text = "hello world, 试试wdasojk2的撒旦就达到库珀扣税的大安东我看到颇为迪帕克王牌大卡普空打开我怕点卡商品考虑大破倭寇打破时空大盘开始大批量看打破时空的赔率看我\n单位卡片我可是大批旅客打开外婆可怕死了，1搭配我看到帕克视频看大破倭寇的排水量可达的卡片网上看到平时离开";
        PDFUtil.addTextToPDF(pdfPath, text, 462, 2406, 1121, 573);
    }

    @Test
    public void extractImageFromPDFTest() {
        String pdfPath = "D:\\tmp\\CCF_000019.pdf";
        String imgPath = "D:\\tmp\\3.jpg";
        PDFUtil.extractImageFromPDF(pdfPath, imgPath, 352, 1699, 1502, 721);
    }

    @Test
    public void pdfTest() throws IOException {
        String pdfPath = "D:\\tmp\\d0e640536a1f4ca193b8e6f0b21f8893.pdf";

        // Create a PDF document object
        PDDocument document = PDDocument.load(new File(pdfPath));

        // Do something with the document, e.g., get the first page
        PDPage page = document.getPage(0);

        // 获取page的宽度和高度
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        System.err.println("width: " + pageWidth + ", height: " + pageHeight);
        // 将page转为图片
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300);
        // 获取图片的大小
        int imgWidth = bim.getWidth();
        int imgHeight = bim.getHeight();
        System.err.println("imgWidth: " + imgWidth + ", imgHeight: " + imgHeight);

        // Don't forget to close the document when you're done
        document.close();

    }

}
