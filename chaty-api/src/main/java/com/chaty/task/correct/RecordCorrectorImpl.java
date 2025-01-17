package com.chaty.task.correct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.chaty.tenant.IgnoreTenant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chaty.completion.CompletionService;
import com.chaty.dto.DocCorrectConfigDTO;
import com.chaty.dto.DocCorrectRecordDTO;
import com.chaty.dto.DocCorrectTaskDTO;
import com.chaty.entity.DocCorrectConfig;
import com.chaty.entity.DocCorrectRecord;
import com.chaty.entity.DocCorrectTask;
import com.chaty.enums.AIModelConsts;
import com.chaty.enums.CorrectEnums.CorrectRecordStatus;
import com.chaty.exception.BaseException;
import com.chaty.mapper.DocCorrectConfigMapper;
import com.chaty.mapper.DocCorrectFileMapper;
import com.chaty.mapper.DocCorrectRecordMapper;
import com.chaty.mapper.DocCorrectTaskMapper;
import com.chaty.service.DocCorrectRecordService;
import com.chaty.service.DocCorrectResultService;
import com.chaty.service.DocCorrectTaskService;
import com.chaty.service.OCRService;
import com.chaty.task.metrics.TaskTimer;
import com.chaty.task.metrics.TaskTimerMetrics;
import com.chaty.util.FileUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecordCorrectorImpl implements RecordCorrector {

    @Resource
    private DocCorrectRecordService docCorrectRecordService;
    @Resource
    private DocCorrectTaskService docCorrectTaskService;
    @Resource
    private DocCorrectTaskMapper docCorrectTaskMapper;
    @Resource
    private DocCorrectRecordMapper docCorrectRecordMapper;
    @Resource
    private DocCorrectConfigMapper docCorrectConfigMapper;
    @Resource
    private Map<String,OCRService> ocrServices;
    @Resource
    private CompletionService completionService;
    @Resource
    private CorrectCacheService correctCacheService;
    @Resource
    private DocCorrectFileMapper docCorrectFileMapper;
    @Resource
    private DocCorrectResultService docCorrectResultService;
    @Resource
    private TaskExecutor correctAreaTaskExecutor;
    @Resource
    private TaskTimerMetrics recordTaskMetrics;
    @Resource
    private TaskTimerMetrics areaTaskMetrics;

    @Value("${file.local.path}")
    public String path;
    @Value("${file.local.ctxpath}")
    public String ctxPath;
    @Value("${server.url}")
    public String serverUrl;

    private final Map<String, String> ocrNameMap = MapUtil.builder(new HashMap<String, String>())
            .put("1", "mathPixOCRService")
            .put("2", "tencentOCRService")
            .put("3", "openaiService")
            .put("4", "paddleOCRService")
            .build();

    @Override
    @IgnoreTenant
    public void correct(DocCorrectRecord record) {
        log.info("DocCorrectTask execute, id: {}, taskId: {}, docname: {}", record.getId(), record.getTaskId(), record.getDocname());
        TaskTimer timer = recordTaskMetrics.createTimer(record.getId());
        DocCorrectRecordDTO recordDTO = BeanUtil.copyProperties(record, DocCorrectRecordDTO.class);
        try {
            recordDTO.setDocParh(recordDTO.getDocurl().substring(ctxPath.length() + 1));
            String taskId = record.getTaskId();
            DocCorrectTask task = docCorrectTaskMapper.selectById(taskId);
            Objects.requireNonNull(task, "未查询到任务信息");
            DocCorrectTaskDTO taskDTO = BeanUtil.copyProperties(task, DocCorrectTaskDTO.class);
            DocCorrectConfig config = docCorrectConfigMapper.selectById(task.getConfigId());
            Objects.requireNonNull(config, "未查询到配置信息");
            DocCorrectConfigDTO configDTO = BeanUtil.copyProperties(config, DocCorrectConfigDTO.class);

            recordDTO.setConfig(configDTO);
            recordDTO.setTask(taskDTO);

            // 批改之前，设置批改次数和批改模型
            beforeCorrect(recordDTO);

            // 添加缓存
            correctCacheService.onCorrectRecord(recordDTO);
            timer.step("DocCorrectTask_InitData"); // 记录初始化任务时间

            // PDF图片裁剪
            JSONArray areasObj = FileUtil.INSTANCE.setDocAreasImg(recordDTO.getDocParh(), configDTO.getAreasObj());
            configDTO.setAreasObj(areasObj);
            timer.step("DocCorrectTask_PdfImgCut"); // 记录裁剪时间

            // 识别试卷姓名
            String identity = ocrForIdentify(recordDTO);
            timer.step("DocCorrectTask_OcrForIdentify"); // 记录识别姓名时间
            // 试卷批改
            // JSONArray reviewed = correctAreas(recordDTO);
            JSONArray reviewed = asyncCorrectAreas(recordDTO);
            timer.step("DocCorrectTask_CorrectAreas"); // 记录批改时间
            // 保存批改结果
            DocCorrectRecord update = new DocCorrectRecord();
            update.setId(record.getId());
            update.setConfigId(config.getId());
            update.setStatus(CorrectRecordStatus.FINISH);
            update.setIdentify(identity);
            update.setReviewed(reviewed.toString());
            onComplete(recordDTO, update);
            timer.step("DocCorrectTask_SaveReviewed"); // 记录保存数据时间
            recordTaskMetrics.closeTimer(timer);
        } catch (Exception e) {
            log.error("DocCorrectTask execute error, id: {}, docname: {}", record.getId(), record.getDocname(), e);
            onError(recordDTO, e);
        } finally {
            correctCacheService.onRecordCorrected(recordDTO); // 更新缓存
        }
    }

    /**
     * 批改之前，设置批改次数和批改模型
     */
    private void beforeCorrect(DocCorrectRecordDTO record) {
        String aimodel = record.getTask().getAimodel();
        Integer times = null;
        if (Objects.equals(aimodel, AIModelConsts.GPT_4O_20240806_3)) {
            aimodel = AIModelConsts.GPT_4O_20240806;
            times = 3; // 三倍批改
        }
        record.setAimodel(aimodel);
        record.setCorrectTimes(times);
    }

    public void onError(DocCorrectRecordDTO record, Exception e) {
        DocCorrectRecord update = new DocCorrectRecord();
        update.setId(record.getId());
        update.setStatus(CorrectRecordStatus.ERROR);
        update.setError(e.getMessage());
        onComplete(record, update);
    }

    /**
     * 识别试卷姓名
     */
    public String ocrForIdentify(DocCorrectRecordDTO record) {
        JSONObject configObj = record.getConfig().getConfigObj();
        DocCorrectTaskDTO task = record.getTask();

        JSONObject nameArea = configObj.getJSONObject("nameArea");
        if (Objects.isNull(nameArea)) {
            return null;
        }
        String img = FileUtil.INSTANCE.docAreaImg(record.getDocParh(), nameArea.getInt("x"), nameArea.getInt("y"),
                nameArea.getInt("width"), nameArea.getInt("height"));
        try {
            return ocrServices.get(ocrNameMap.get(task.getOcrType()))
                .ocrForText(String.format("%s%s/%s", serverUrl, ctxPath, img));
        } catch (Exception e) {
            // 调用腾讯的...
            log.error("ocrForIdentify error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 批改区域
     */
    public JSONArray correctAreas(DocCorrectRecordDTO record) {
        DocCorrectConfigDTO config = record.getConfig();
        JSONArray areas = config.getAreasObj();

        JSONArray reviewed = new JSONArray();
        for (int areaIdx = 0; areaIdx < areas.size(); areaIdx++) {
            JSONObject areaObj = areas.getJSONObject(areaIdx);
            JSONObject areaRes = correctArea(record, areaObj, areaIdx);
            reviewed.add(areaRes);
        }
        return reviewed;
    }

    /**
     * 异步批改区域
     */
    public JSONArray asyncCorrectAreas(DocCorrectRecordDTO record) {
        DocCorrectConfigDTO config = record.getConfig();
        JSONArray areas = config.getAreasObj();

        List<CompletableFuture<JSONObject>> futures = new ArrayList<>();
        for (int areaIdx = 0; areaIdx < areas.size(); areaIdx++) {
            JSONObject areaObj = areas.getJSONObject(areaIdx);
            int finalAreaIdx = areaIdx; // effectively final variable for lambda
            futures.add(CompletableFuture.supplyAsync(() -> correctArea(record, areaObj, finalAreaIdx), 
                    correctAreaTaskExecutor));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toCollection(JSONArray::new)))
                .join();
    }

    /**
     * 批改区域
     */
    public JSONObject correctArea(DocCorrectRecordDTO record, JSONObject areaObj, Integer areaIdx) {
        log.info("DocCorrectTask correctAreas, id: {}, areaIdx: {}", record.getId(), areaIdx);
        TaskTimer timer = areaTaskMetrics.createTimer(String.format("%s_%s", record.getId(), areaIdx));

        JSONObject areaRes = new JSONObject();
        areaRes.set("status", "1"); // status(1：正常，2：异常)
        areaRes.set("areaIdx", areaIdx);
        JSONArray questions = areaObj.getJSONArray("questions");
        try {
            boolean enabled = areaObj.getBool("enabled", true);
            if (!enabled) {
                return areaRes;
            }
            Integer areaType = areaObj.getInt("areaType", 1);
            if (areaType == 1) {
                // 批改
                if (Objects.isNull(record.getCorrectTimes())) {
                    completionService.correctRecordArea(record, areaObj, areaRes);
                    timer.step("DocCorrectArea_AICorrect");
                } else {
                    // 多次批改结果比较
                    doMultiCorrectArea(record.getCorrectTimes(), record, areaObj, areaRes);
                    timer.step("DocCorrectArea_AICorrect_3");
                }
                areaTaskMetrics.closeTimer(timer);
            } else if (areaType == 2) {
                // 答题卡区域批改
                completionService.correctAnswerCard(record, areaObj, areaRes);
            } else if (areaType == 3) {
                // 写作题处理
                completionService.correctWriteQsTwiceMergers(record, areaObj, areaRes);
            } else if (areaType == 4) {
                // 老师已有分数识别,一个区域只有一个题目，不需要多次批改，题目用来确定位置和分数
                completionService.correctScoreArea(record, areaObj, areaRes);
            }
        } catch (Exception e) {
            log.error("DocCorrectTask correctAreas error, id: {}, areaIdx: {}", record.getId(), areaIdx, e);
            areaRes.set("status", "2");
            areaRes.set("error", e.getMessage());
            List<JSONObject> defaultReviewed = questions.stream()
                    .map(qs -> new JSONObject().set("isCorrect", "Y"))
                    .collect(Collectors.toList());
            areaRes.set("reviewed", defaultReviewed);
        }

        correctCacheService.onAreaCorrected(record); // 更新缓存
        return areaRes;
    }

    /**
     * 执行多次批改，批改结果比较
     */
    private void doMultiCorrectArea(Integer correctTimes, DocCorrectRecordDTO record, JSONObject areaObj,
            JSONObject areaRes) {
        List<JSONArray> reviewedList = new ArrayList<>();
        int errTimes = 0;
        // 进行多次批改
        for (int time = 0; time < correctTimes; time++) {
            log.info("DocCorrectTask correctAreas, id: {}, areaObj: {}, time: {}", record.getId(), areaObj, time);
            record.setTemperature(time * 0.5f);
            try {
                completionService.correctRecordArea(record, areaObj, areaRes);
            } catch (Exception e) {
                log.error("DocCorrectTask correctAreas error, id: {}, areaObj: {}, time: {}", record.getId(), areaObj,
                        time, e);
                errTimes++;
                if (errTimes >= correctTimes) {
                    // 全部失败，抛出批改失败错误
                    throw new BaseException(e.getMessage());
                }
            }
            reviewedList.add(areaRes.getJSONArray("reviewed")); // 保存批改结果
        }
        // 比较批改结果
        JSONArray questions = areaObj.getJSONArray("questions");
        JSONArray reviewedRes = new JSONArray();
        for (int qsIdx = 0; qsIdx < questions.size(); qsIdx++) {
            int trueNum = 0;
            JSONObject trueReviewed = null;
            JSONObject falseReviewed = null;
            for (JSONArray reviewed : reviewedList) {
                if (Objects.isNull(reviewed)) {
                    trueNum++;
                    continue;
                }
                JSONObject reviewedQs = reviewed.getJSONObject(qsIdx);
                if (Objects.isNull(reviewedQs)) {
                    trueNum++;
                    continue;
                }
                if ("Y".equals(reviewedQs.getStr("isCorrect"))) {
                    trueNum++;
                    trueReviewed = reviewedQs;
                } else {
                    falseReviewed = reviewedQs;
                }
            }
            JSONObject reviewedQs = null; // 这道题的最终批改结果
            if (trueNum >= correctTimes / 2f) {
                reviewedQs = trueReviewed;
            } else {
                reviewedQs = falseReviewed;
            }
            reviewedQs = reviewedQs == null ? new JSONObject() : reviewedQs;
            reviewedQs.set("trueNum", trueNum);
            reviewedRes.add(reviewedQs);
        }
        areaRes.set("reviewed", reviewedRes);
        areaRes.set("reviewList", reviewedList);
    }

    public void onComplete(DocCorrectRecordDTO record, DocCorrectRecord update) {
        docCorrectRecordMapper.updateById(update);
        int completed = docCorrectTaskMapper.tryComplete(record.getTaskId());
        if (completed > 0) {
            // 如果任务批改完成并且关联了文件，尝试更新文件的状态
            if (Objects.nonNull(record.getTask().getFileId())) {
                docCorrectFileMapper.tryComplete(record.getTask().getFileId());
            }
            // 保存批改结果 并发问题；难搞...
            // docCorrectResultService.saveByTask(record.getTaskId());
        }
    }

    public List<JSONObject> getMarkAreas(JSONObject areaObj, JSONObject area) {
        return areaObj.getJSONArray("questions").stream()
                .map(qs -> {
                    JSONObject qsPosArea = ((JSONObject) qs).getJSONObject("qsPosArea");
                    if (Objects.nonNull(qsPosArea)) {
                        qsPosArea.set("x", qsPosArea.getInt("x") - area.getInt("x"));
                        qsPosArea.set("y", qsPosArea.getInt("y") - area.getInt("y"));
                    }
                    return qsPosArea;
                })
                .filter(a -> Objects.nonNull(a))
                .collect(Collectors.toList());
    }

    @Override
    public void correctDocName(DocCorrectTaskDTO param) {
        DocCorrectTask task = docCorrectTaskMapper.selectById(param.getId());
        if (Objects.isNull(task)) {
            throw new BaseException("未查询到任务信息");
        }
        if (Objects.isNull(task.getConfigId())) {
            throw new BaseException("为查询到试卷配置");
        }
        DocCorrectTaskDTO taskDTO = BeanUtil.toBean(task, DocCorrectTaskDTO.class);
        taskDTO.setOcrType("3");
        List<DocCorrectRecord> records = docCorrectRecordService.selectByTaskId(param.getId(), null);
        DocCorrectConfig config = docCorrectConfigMapper.selectById(task.getConfigId());
        DocCorrectConfigDTO configDTO = BeanUtil.toBean(config, DocCorrectConfigDTO.class);
        records.forEach(record -> {
            DocCorrectRecordDTO recordDTO = BeanUtil.toBean(record, DocCorrectRecordDTO.class);
            recordDTO.setTask(taskDTO);
            recordDTO.setConfig(configDTO);
            recordDTO.setDocParh(recordDTO.getDocurl().substring(ctxPath.length() + 1));
            String name = ocrForIdentify(recordDTO);
            if (Objects.nonNull(name)) {
                docCorrectRecordMapper.update(Wrappers.lambdaUpdate(DocCorrectRecord.class)
                        .set(DocCorrectRecord::getIdentify, name)
                        .eq(DocCorrectRecord::getId, record.getId()));
            }
        });
    }

}
