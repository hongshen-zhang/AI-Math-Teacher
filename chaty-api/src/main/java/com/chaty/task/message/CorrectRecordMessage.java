package com.chaty.task.message;

import com.chaty.entity.DocCorrectRecord;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 试卷批改消息
 */
@Data
@NoArgsConstructor
public class CorrectRecordMessage {

    private DocCorrectRecord record;

    public CorrectRecordMessage(DocCorrectRecord record) {
        this.record = record;
    }

}
