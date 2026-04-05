package com.studyflow.backend.shared.event;

public class StudyItemCreatedEvent extends AbstractStudyItemEvent {

    public StudyItemCreatedEvent(Object source, Long studyItemId, Long userId) {
        super(source, studyItemId, userId);
    }
}

