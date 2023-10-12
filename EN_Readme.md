# SolveGPT——AI Math Teacher

🌐 [English](./EN_Readme.md) | [中文](./README.md)

By : Xinjin(Synthia) Li

![](https://img.shields.io/badge/License-MIT-lightgrey)
![](https://img.shields.io/badge/Version-v0.0.1-orange)

[SolveGPT](http://110.40.186.52:10001/solve)
### For Problem solving
Facilitate an entire process, from capturing math problems via photos to producing automated teaching materials. Its edge over other tools lies in its accuracy and the intelligibility of its answers.

![image](https://github.com/hongshen-zhang/AI-Math-Teacher/assets/51727955/2007fa72-04de-4ad8-8e3c-32f37c42f03d)

## Resource

Demo Video：[SolveGPT](https://www.bilibili.com/video/BV1yj411R7FR/?share_source=copy_web&vd_source=2402ea50d5e761d0c54f9f9cb8f35a85)

Website ：[SolveGPT](http://110.40.186.52:10001/solve)

Android : [SolveGPT](https://github.com/hongshen-zhang/AI-Math-Teacher/releases/tag/v0.0.1)

Github：[https://github.com/hongshen-zhang/SolveGPT](https://github.com/hongshen-zhang/AI-Math-Teacher)


# Demand
1. No Solution for Problem.
![image](https://github.com/hongshen-zhang/NeoWizard/assets/51727955/1503150f-9a30-46a6-9cd7-c2ad5f51a856)
2. Due to unfamiliarity with the relevant theorems, even having the answer doesn't make it understandable.
![image](https://github.com/hongshen-zhang/NeoWizard/assets/51727955/3d78c150-a7c9-451d-a0d2-c661a16bb6d1)

## Spot 
![image](https://github.com/hongshen-zhang/NeoWizard/assets/51727955/932f440f-5402-4c4a-9bea-efa4ee707132)
![image](https://github.com/hongshen-zhang/NeoWizard/assets/51727955/f48ce64e-99e2-4e9c-be7b-4e18b4065b68)
![image](https://github.com/hongshen-zhang/NeoWizard/assets/51727955/67e467cd-0535-44a1-bdbc-e5328dcf0a66)
![image](https://github.com/hongshen-zhang/NeoWizard/assets/51727955/5f059fec-d526-4d4e-92fc-a2fc5664b68e)


## Plan 


| Requirement  | Description                                                 | Time | Progress |
| ------------ | ----------------------------------------------------------- | ---- | -------- |
| Basic Feature | Website launch                                              | July | ✅       |
| Basic Feature | Android version release                                     | Aug  | ✅       |
| Basic Feature | Automatic beamer generation                                 | Aug  | ✅       |
| Basic Feature | Support for Chinese and English, dark mode, different levels| Aug  | ✅       |
| Basic Feature | Launch web3 payment function                                | Aug  | ✅       |
| Patent 1/2   | A method for generating math problem answers based on multi-model adversarial approaches | Sept | ✅       |
| Patent 2/2   | An automated mathematical lecture note generation method based on beamer | Sept | ✅       |
| Code Optimization | Speed up execution                                       | Sept | ❌       |
| Domain Registration | solvegpt.cn                                             | Sept | ✅       |
| Official Launch | Add login function                                         | Oct  | ❌       |


## How to Use:

### Please add the OCR Key and OPENAI Key

```
1. OCR secret key
./solvegpt/main.py line 95:
def tencent_ocr(img_base64):
    cred = credential.Credential(
        "", ""
    )
 
2. OpenAI secret key
./solvegpt/openai_config.json line 2:
{
    "api_key": "",
```



