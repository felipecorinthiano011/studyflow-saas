package com.studyflow.backend.common.mapper;

import com.studyflow.backend.domain.study.entity.StudyItem;
import com.studyflow.backend.shared.dto.StudyItemResponseDTO;

/**
 * Stateless mapper for StudyItem → StudyItemResponseDTO conversions.
 * Centralises the builder call that was previously a private method in StudyItemService.
 */
public final class StudyItemMapper {

    private StudyItemMapper() {
        throw new AssertionError("Utility class");
    }

    public static StudyItemResponseDTO toDTO(StudyItem item) {
        return StudyItemResponseDTO.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .createdAt(item.getCreatedAt())
                .build();
    }
}

