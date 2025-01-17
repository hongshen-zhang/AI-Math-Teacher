package com.chaty.enums;

import java.util.Set;

import cn.hutool.core.collection.CollUtil;

public interface AIModelConsts {

    String GPT_4_V = "gpt-4-vision-preview";

    String GPT_4O = "gpt-4o";

    String GPT_4O_MINI = "gpt-4o-mini";

    String GPT_4O_20240806 = "gpt-4o-2024-08-06";

    String GPT_4O_20240806_3 = "gpt-4o-2024-08-06_3";  // 批改模型，三倍批改

    String GEMINI_15_PRO = "gemini-1.5-pro";

    String GEMINI_15_FLASH = "gemini-1.5-flash";

    String GEMINI_20_FALSH_EXP = "gemini-2.0-flash-exp";

    Set<String> visionModels = CollUtil.newHashSet(GPT_4_V, GPT_4O, GPT_4O_MINI, GPT_4O_20240806, GPT_4O_20240806_3, GEMINI_15_PRO, GEMINI_15_FLASH, GEMINI_20_FALSH_EXP, "Claude 3 Opus", "Claude 3 Sonnet");

    Set<String> functionCallModels = CollUtil.newHashSet("gpt-4", "gpt-3.5-turbo", "gpt-4-turbo", "ERNIE-Bot");

    Set<String> claudeModels = CollUtil.newHashSet("Claude 3 Opus", "Claude 3 Sonnet");

    Set<String> openaiVisionModels = CollUtil.newHashSet(GPT_4O, GPT_4_V, GPT_4O_MINI, GPT_4O_20240806, GPT_4O_20240806_3, GEMINI_15_PRO, GEMINI_15_FLASH, GEMINI_20_FALSH_EXP);

}
