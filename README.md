![image](https://github.com/hongshen-zhang/AI-Math-Teacher/assets/51727955/f75be87d-2907-4ff3-9531-7ed63ca1541c)![image](https://github.com/hongshen-zhang/AI-Math-Teacher/assets/51727955/72b447ee-1cdc-4883-aa17-bd49bfa26ae4)# AI-Math-Teacher——AI集智数学老师
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
![图片](https://github.com/hongshen-zhang/Unique-hackday_solvegpt/assets/51727955/000343c5-5662-4b8a-adb0-3fd8c98fde7f)

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
