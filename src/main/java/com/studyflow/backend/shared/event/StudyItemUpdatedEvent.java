package com.studyflow.backend.shared.event;

public class StudyItemUpdatedEvent extends AbstractStudyItemEvent {

    public StudyItemUpdatedEvent(Object source, Long studyItemId, Long userId) {
        super(source, studyItemId, userId);
    }
}

