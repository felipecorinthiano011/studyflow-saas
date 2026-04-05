package com.studyflow.backend.shared.event;

import org.springframework.context.ApplicationEvent;

/**
 * Base class for all StudyItem domain events.
 * Eliminates duplication across Created/Updated/Deleted event classes.
 */
public abstract class AbstractStudyItemEvent extends ApplicationEvent {

    private final Long studyItemId;
    private final Long userId;

    protected AbstractStudyItemEvent(Object source, Long studyItemId, Long userId) {
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

