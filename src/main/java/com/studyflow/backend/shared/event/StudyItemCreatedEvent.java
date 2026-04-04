package com.studyflow.backend.shared.event;

import org.springframework.context.ApplicationEvent;

public class StudyItemCreatedEvent extends ApplicationEvent {

    private final Long studyItemId;
    private final Long userId;

    public StudyItemCreatedEvent(Object source, Long studyItemId, Long userId) {
        super(source);
        this.studyItemId = studyItemId;
        this.userId = userId;
    }

    public Long getStudyItemId() {
        return studyItemId;
    }

    public Long getUserId() {
        return userId;
    }
}

