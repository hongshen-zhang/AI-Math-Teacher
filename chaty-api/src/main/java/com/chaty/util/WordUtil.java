package com.chaty.util;
import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.chaty.dto.EssayWordDTO;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.policy.HackLoopTableRenderPolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Random;


public class WordUtil {

    private static final Log log = LogFactory.getLog(WordUtil.class);

    public static void processWordTemplateIntoPdf(String templateName, EssayWordDTO data, String wordOutPath, String pdfOutPath) {
        try (InputStream templateStream = WordUtil.class.getClassLoader().getResourceAsStream("templates/" + templateName)) {
            if (templateStream == null) {
                throw new FileNotFoundException("Template not found: " + templateName);
            }
            HackLoopTableRenderPolicy policy = new HackLoopTableRenderPolicy();
            Configure config = Configure.newBuilder().bind("list", policy).build();
            XWPFTemplate template = XWPFTemplate.compile(templateStream, config).render(data);
            // 继续处理模板
            try {
                FileOutputStream out = new FileOutputStream(wordOutPath);//要导出的文件名
                template.write(out);
                out.flush();
                out.close();
                template.close();
                wordToPdf(wordOutPath, pdfOutPath);
            } catch (IOException e) {
                log.error("Error writing template", e);
            }
        } catch (IOException e) {
            log.error("Error loading template", e);
        }
    }

    /**
     * 加载license 用于破解 不生成水印
     *
     * @author LCheng
     * @date 2020/12/25 13:51
     */
    public static void getLicense() {
        try (InputStream is = WordUtil.class.getClassLoader().getResourceAsStream("License.xml")) {
            License license = new License();
            license.setLicense(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * word转pdf
     *
     * @param wordPath word文件保存的路径
     * @param pdfPath  转换后pdf文件保存的路径
     * @author LCheng
     * @date 2020/12/25 13:51
     */
    public static void wordToPdf(String wordPath, String pdfPath) throws IOException {
        getLicense();
        File file = new File(pdfPath);
        try (FileOutputStream os = new FileOutputStream(file)) {
            Document doc = new Document(wordPath);
            doc.save(os, SaveFormat.PDF);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args) throws Exception {
////        WordUtil.test();
//        wordToPdf("C:\\Users\\Administrator\\Desktop\\chaty\\chaty-api\\src\\main\\resources\\templates\\essay.docx","C:\\Users\\Administrator\\Desktop\\test.pdf");
//    }

    public static void main(String[] args) {
        // 定义数组大小
        int size = 10;
        int[] array = new int[size];

        // 初始化数组，填充1到10000
        for (int i = 0; i < size; i++) {
            array[i] = i + 1;
        }



        // 打乱数组
        shuffleArray(array);
        System.out.println(size);
        for (int i = 1; i < size; i++) {
            int cnt = 0;
            for(int j = 0; j < i; j++) {
                if (array[j] < array[i]) {
                    cnt++;
                }
            }
            System.out.println(cnt);
        }
    }

    /**
     * 使用 Fisher-Yates 算法打乱数组
     * @param array 要打乱的整数数组
     */
    public static void shuffleArray(int[] array) {
        Random rand = new Random();

        for (int i = array.length - 1; i > 0; i--) {
            // 生成0到i之间的随机索引
            int j = rand.nextInt(i + 1);

            // 交换 array[i] 和 array[j]
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}