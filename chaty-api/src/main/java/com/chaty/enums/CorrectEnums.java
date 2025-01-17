package com.chaty.enums;

public interface CorrectEnums {

    interface CorrectEventType {
        String CORRECT = "correct";
        String RECORD = "record";
    }

    interface CorrectTakStatus {
        Integer WAIT = 1;
        Integer PROCESSING = 2;
        Integer FINISH = 3;
    }

    interface CorrectRecordStatus {
        Integer UNCORRECT = 1;
        Integer WAIT = 2;
        Integer PROCESSING = 3;
        Integer FINISH = 4;
        Integer ERROR = 5;
    }

}
