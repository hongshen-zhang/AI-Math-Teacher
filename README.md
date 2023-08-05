# AI-Math-Teacher——AI集智数学老师

![](https://img.shields.io/badge/License-MIT-lightgrey)
![](https://img.shields.io/badge/Version-v0.0.1-orange)

## 基于大语言模型的数学老师

[solvegpt](http://118.89.117.111/solvegpt/index.html)! 是一款基于大语言模型的数学老师，做题更准，显示正确率，并自动生成数学讲义。

我们的愿景是让数学不再那么高冷，让我们在学习数学的过程中，不仅能得到答案，还能看懂答案！

## Resource 资源

Demo 视频：[solvegpt演示](https://www.bilibili.com/video/BV1yj411R7FR/?share_source=copy_web&vd_source=2402ea50d5e761d0c54f9f9cb8f35a85)

网页版：[solvegpt网页](http://118.89.117.111/solvegpt/index.html)

安卓版:[solvegpt安卓](http://118.89.117.111/solvegpt/index.html)

Github：[https://github.com/hongshen-zhang/AI-Math-Teacher](https://github.com/hongshen-zhang/AI-Math-Teacher)


# 实际需求
1. 题目没答案。
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/2ab8be8e-561a-4237-9eb9-55fd11b4e322)
2. 由于不知道相关定理，有答案也看不懂。
![图片](https://github.com/hongshen-zhang/AI-Math-Teacher/assets/51727955/eb6adece-c718-46ac-a1e7-14a63d2725c9)

## 产品亮点
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/4ae3cff4-272d-4bcc-b6a9-98a667d89ec1)
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/7aff38a8-95d8-42ef-8a6d-453d101fb1c0)
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/7777975e-be56-4f78-a2f6-7607d85b3f57)
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/5435abf1-5a8f-4285-b4e4-e894bc64de28)

## 计划

目前项目比较早期，也欢迎大家提需求

| 需求         | 描述                                                     | 时间 | 进度 |
| ------------ | -------------------------------------------------------- | ---- | ---- |
| 基本功能     | 网页端上线           | 7 月 | ✅   |
| 基本功能     | 安卓端上限                             | 8 月 | ✅   |
| 自动生成beamer      | 一种基于多模型对抗的数学问题答案生成方法                   | 8 月 | ✅   |
| 写专利 1/2       | 一种基于多模型对抗的数学问题答案生成方法                   | 8 月 | ✅   |
| 写专利 2/2     | 一种基于beamer的自动化数学课件生成方法              | 8 月 |✅   |
| 代码优化   | 加快运行速度                                 | 9 月 | ❌   |
| 正式上线 | 增加登录功能                             | 10 月 | ❌   |


使用方法：

## 请补充OCR Key和OPENAI Key

```
1. ocr secret key
./solvegpt/main.py line 95:
def tencent_ocr(img_base64):
    cred = credential.Credential(
        "", ""
    )
 
2. openai secret key
./solvegpt/openai_config.json line 2:
{
    "api_key": "",
```



