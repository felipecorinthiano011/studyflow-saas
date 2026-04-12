package com.studyflow.backend.shared.event;

import java.io.Serial;

public class StudyItemUpdatedEvent extends AbstractStudyItemEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    public StudyItemUpdatedEvent(Object source, Long studyItemId, Long userId) {
        super(source, studyItemId, userId);
    }
}

