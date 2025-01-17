package com.chaty.form;

import com.chaty.enums.AIModelConsts;

import lombok.Data;

@Data
public class ExtraQsForm {

    private String aimodel = AIModelConsts.GPT_4O;

    private String img;

    private String imgStr; // Base64字符串

    private String prompt;

}
