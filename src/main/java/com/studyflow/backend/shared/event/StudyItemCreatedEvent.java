package com.studyflow.backend.shared.event;

import java.io.Serial;

public class StudyItemCreatedEvent extends AbstractStudyItemEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    public StudyItemCreatedEvent(Object source, Long studyItemId, Long userId) {
        super(source, studyItemId, userId);
    }
}

