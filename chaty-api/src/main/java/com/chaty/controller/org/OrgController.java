package com.chaty.controller.org;

import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chaty.common.BaseResponse;
import com.chaty.dto.OrgQuestionDTO;
import com.chaty.entity.OrgCorrectResult;
import com.chaty.entity.OrgQuestions;
import com.chaty.exception.BaseException;
import com.chaty.service.OrgService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/org")
@RestController
public class OrgController {

    @Resource
    private OrgService orgService;

    @PostMapping("/question/save")
    public BaseResponse<?> question(@RequestBody OrgQuestionDTO params) {
        if (Objects.isNull(params.getQuestion())) {
            throw new BaseException("question is required");
        }
        if (Objects.isNull(params.getCorrectAnswer())) {
            throw new BaseException("correctAnswer is required");
        }
        if (Objects.isNull(params.getScore())) {
            throw new BaseException("score is required");
        }

        OrgQuestions res = orgService.saveQuestion(params);
        return BaseResponse.ok(res);
    }

    @PostMapping("/correct")
    public BaseResponse<?> correct(@RequestBody OrgQuestionDTO params) {
        if (Objects.isNull(params.getId())) {
            throw new BaseException("id is required");
        }
        if (Objects.isNull(params.getCorrectImage())) {
            throw new BaseException("correctImage is required");
        }

        OrgCorrectResult res = orgService.correct(params);
        return BaseResponse.ok(res);
    }

}