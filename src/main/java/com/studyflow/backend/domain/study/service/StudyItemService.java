package com.studyflow.backend.domain.study.service;

import com.studyflow.backend.shared.dto.StudyItemRequestDTO;
import com.studyflow.backend.shared.dto.StudyItemResponseDTO;
import com.studyflow.backend.domain.study.entity.StudyItem;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.study.repository.StudyItemRepository;
import com.studyflow.backend.domain.user.repository.UserRepository;
import com.studyflow.backend.shared.event.StudyItemCreatedEvent;
import com.studyflow.backend.shared.event.StudyItemDeletedEvent;
import com.studyflow.backend.shared.event.StudyItemUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudyItemService {

    private final StudyItemRepository studyItemRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @CacheEvict(value = "study-items", allEntries = true)
    public StudyItemResponseDTO create(StudyItemRequestDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long orgId = user.getOrganization() != null ? user.getOrganization().getId() : null;

        StudyItem studyItem = StudyItem.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .user(user)
                .orgId(orgId)
                .build();

        StudyItem saved = studyItemRepository.save(studyItem);
        eventPublisher.publishEvent(new StudyItemCreatedEvent(this, saved.getId(), userId));
        return mapToResponse(saved);
    }

    @CacheEvict(value = "study-items", allEntries = true)
    public StudyItemResponseDTO update(Long id, StudyItemRequestDTO dto, Long userId) {
        StudyItem item = studyItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());

        StudyItem saved = studyItemRepository.save(item);
        eventPublisher.publishEvent(new StudyItemUpdatedEvent(this, saved.getId(), userId));
        return mapToResponse(saved);
    }

    @CacheEvict(value = "study-items", allEntries = true)
    public void delete(Long id, Long userId) {
        StudyItem item = studyItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        studyItemRepository.delete(item);
        eventPublisher.publishEvent(new StudyItemDeletedEvent(this, id, userId));
    }

    @CacheEvict(value = "study-items", allEntries = true)
    public void deleteAll(Long userId) {
        studyItemRepository.deleteAllByUserId(userId);
        eventPublisher.publishEvent(new StudyItemDeletedEvent(this, null, userId));
    }

    @Cacheable(value = "study-items",
            key = "#userId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<StudyItemResponseDTO> findAllByUser(Long userId, Pageable pageable) {
        return studyItemRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    private StudyItemResponseDTO mapToResponse(StudyItem studyItem) {
        return StudyItemResponseDTO.builder()
                .id(studyItem.getId())
                .title(studyItem.getTitle())
                .description(studyItem.getDescription())
                .createdAt(studyItem.getCreatedAt())
                .build();
    }
}
