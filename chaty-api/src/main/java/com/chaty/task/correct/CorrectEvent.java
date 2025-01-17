package com.chaty.task.correct;

import lombok.Data;

@Data
public class CorrectEvent {

    private String type;

    public static CorrectEvent type(String type) {
        CorrectEvent event = new CorrectEvent();
        event.setType(type);
        return event;
    }

}
