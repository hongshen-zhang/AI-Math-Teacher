# AI-Math-Teacher——AI集智数学老师

![](https://img.shields.io/badge/License-MIT-lightgrey)
![](https://img.shields.io/badge/Version-v0.0.1-orange)

Demo已开放使用: [solvegpt](http://118.89.117.111/solvegpt/index.html)!

## 基于大语言模型的数学老师

[solvegpt](http://118.89.117.111/solvegpt/index.html)! 是一款基于大语言模型的数学老师，做题更准，显示正确率，并自动生成数学讲义。

我们的愿景是让数学不再那么高冷，让我们在学习数学的过程中，不仅能得到答案，还能看懂答案！

## Resource 资源

Demo 视频：[solvegpt演示](https://www.bilibili.com/video/BV1yj411R7FR/?share_source=copy_web&vd_source=2402ea50d5e761d0c54f9f9cb8f35a85)

网页版：[solvegpt网页](http://118.89.117.111/solvegpt/index.html)

安卓版:[solvegpt安卓](http://118.89.117.111/solvegpt/index.html)]

Github：[https://github.com/PromptsLego/PromptsLego](https://github.com/hongshen-zhang/AI-Math-Teacher)

## 产品亮点

1. 多模态输入(Multimodal Input)，用于识别图片题目公式与文字(基于腾讯云OCR)
2. 多模型联合对抗求解(Adversarial Learning)，用于提高求解准确率(gpt-3.5-turbo,gpt-4,gpt-4-0613)
3. 准确率智能判断(Accuracy Analysis)，用于评估解题答案的准确性(Accuracy 0% - 100%)
4. 知识点讲解(Knowledge Explanation),全自动化生成数学讲义

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

## 开发者部署流程

本项目使用[Vite](https://github.com/vitejs/vite.git).

使用方法：

```shell
yarn install
yarn dev
```

或者

```shell
npm install
npm run dev
```

部署如果需要使用优化的功能，需要在 `src/config.ts` 中填入自己的 [PromptsPerfect](https://promptperfect.jinaai.cn/) 的 Api key

## 致谢

感谢大家对我们项目的大力支持，感谢 [OPS 提示词工作室](https://moonvy.com/apps/ops/) 给我们的灵感

## 联系方式

![](./docs/assets/welcome.png)

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=PromptsLego/PromptsLego&type=Date)](https://star-history.com/#PromptsLego/PromptsLego&Date)







# AI-Math-Teacher——AI集智数学老师
喵喵汪汪

# 背景介绍
目前，一种主流的观点是:chatgpt能帮我们解决数学作业问题(90%的国外学生都用chatgpt写作业)。
而实际上，目前以chatgpt为首的大模型在求解数学问题方面并不容易，答案做不对，还看不懂。

使用大模型解题的完整流程是:
第一步: 打开vpn。(ip地址管控严格);
第二步: 登陆openai。(每次需要重新输入账号);
第三步: 将文字和公式输入对话框。(尤其是难以输入复杂公式与选择题);
第四步: 得到一个不太准确的结果，答案不一定能看得懂。(或者说是无法评估的答案)。

## 整个过程存在许多障碍
1. 网络限制: 需要连接外网。
2. 账号限制: 需要注册海外Chatgpt账号。
3. 输入复杂: 手动将文字和公式输入Chatgpt。
4. 结果不准：Chatgpt目前在解决理科题目时，准确率低。(高考理科填空题和大题准确率不足20%)

## 因此,我们开发了[solvegpt](http://118.89.117.111/solvegpt/index.html)，一款AI集智数学老师。

1. 无网络限制: 部署在腾讯云(使用香港节点中转)。
2. 无账号限制: 我们目前免费开放使用(我垫钱，8月4日又买了200刀的额度,gpt4-apikey)。
3. 输入简单: 支持识别图片题目公式与文字。
4. 结果判断：基于多模型(gpt3.5,gpt4)联合讨论，我们能够更准确的求解(相比于直接求解)。对于实在无法解决的问题，我们输出了准确率，给学生提供参考。

# 软件部署
## 1. 网页端: 开发了 [solvegpt](http://118.89.117.111/solvegpt/index.html)
![图片](https://github.com/hongshen-zhang/AI-Math-Teacher/assets/51727955/7da410ba-6133-4d76-8236-bbade6c56e9d)

## 2. 安卓端: 开发了 Android端App-Solvegpt(Release now,v1.0.0)
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/93a552f2-0f0d-4d4b-922e-0f38c291bb19)

# 产品灵感(做题更准+定理解释）
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/2ab8be8e-561a-4237-9eb9-55fd11b4e322)
![图片](https://github.com/hongshen-zhang/AI-Math-Teacher/assets/51727955/eb6adece-c718-46ac-a1e7-14a63d2725c9)

# 产品功能:
1. 多模态输入(Multimodal Input)，用于识别图片题目公式与文字(基于腾讯云OCR)
2. 多模型联合对抗求解(Adversarial Learning)，用于提高求解准确率(gpt-3.5-turbo,gpt-4,gpt-4-0613)
3. 准确率智能判断(Accuracy Analysis)，用于评估解题答案的准确性(Accuracy 0% - 100%)
4. 知识点讲解(Knowledge Explanation),全自动化生成数学讲义
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/5624a10a-fc55-4ae8-ae6b-90fc1d69a6f9)

# 产品亮点
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/4ae3cff4-272d-4bcc-b6a9-98a667d89ec1)
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/7aff38a8-95d8-42ef-8a6d-453d101fb1c0)
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/7777975e-be56-4f78-a2f6-7607d85b3f57)
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/5435abf1-5a8f-4285-b4e4-e894bc64de28)

# 竞品对比
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/2d5a7d85-754c-4e55-a99b-d6943679fc0f)
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/68d1a4ba-1134-4d50-9a6a-a30f58439b3d)

# AI大模型相关
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/763ffa24-f66e-4df1-bf16-209bc454ae1b)

---

# 代码运行说明
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
