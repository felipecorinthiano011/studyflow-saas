package com.studyflow.backend.shared.event;

public class StudyItemDeletedEvent extends AbstractStudyItemEvent {

    public StudyItemDeletedEvent(Object source, Long studyItemId, Long userId) {
        super(source, studyItemId, userId);
    }
}

