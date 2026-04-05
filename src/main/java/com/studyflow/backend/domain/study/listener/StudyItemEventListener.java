package com.studyflow.backend.domain.study.listener;

import com.studyflow.backend.domain.audit.service.AuditLogService;
import com.studyflow.backend.shared.event.StudyItemCreatedEvent;
import com.studyflow.backend.shared.event.StudyItemDeletedEvent;
import com.studyflow.backend.shared.event.StudyItemUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyItemEventListener {

    private static final String ENTITY_TYPE = "StudyItem";

    private final AuditLogService auditLogService;

    @EventListener
    public void onCreated(StudyItemCreatedEvent event) {
        auditLogService.logAction(event.getUserId(), "STUDY_ITEM_CREATED",
                ENTITY_TYPE, event.getStudyItemId(), null);
    }

    @EventListener
    public void onUpdated(StudyItemUpdatedEvent event) {
        auditLogService.logAction(event.getUserId(), "STUDY_ITEM_UPDATED",
                ENTITY_TYPE, event.getStudyItemId(), null);
    }

    @EventListener
    public void onDeleted(StudyItemDeletedEvent event) {
        auditLogService.logAction(event.getUserId(), "STUDY_ITEM_DELETED",
                ENTITY_TYPE, event.getStudyItemId(), null);
    }
}

