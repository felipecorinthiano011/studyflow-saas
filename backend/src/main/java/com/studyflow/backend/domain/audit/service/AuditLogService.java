package com.studyflow.backend.domain.audit.service;

import com.studyflow.backend.domain.audit.entity.AuditLog;
import com.studyflow.backend.domain.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void logAction(Long userId, String action,
            String entityType, Long entityId, String details) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .timestamp(Instant.now())
                .details(details)
                .build();
        auditLogRepository.save(log);
    }
}

