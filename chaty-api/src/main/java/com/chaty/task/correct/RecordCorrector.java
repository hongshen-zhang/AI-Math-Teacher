package com.chaty.task.correct;

import com.chaty.dto.DocCorrectRecordDTO;
import com.chaty.dto.DocCorrectTaskDTO;
import com.chaty.entity.DocCorrectRecord;

public interface RecordCorrector {

    void correct(DocCorrectRecord record);

    String ocrForIdentify(DocCorrectRecordDTO record);

    void correctDocName(DocCorrectTaskDTO param);

}
