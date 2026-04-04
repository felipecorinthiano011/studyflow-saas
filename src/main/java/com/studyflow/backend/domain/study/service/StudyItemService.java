package com.studyflow.backend.domain.study.service;

import com.studyflow.backend.shared.dto.StudyItemRequestDTO;
import com.studyflow.backend.shared.dto.StudyItemResponseDTO;
import com.studyflow.backend.domain.study.entity.StudyItem;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.study.repository.StudyItemRepository;
import com.studyflow.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyItemService {

    private final StudyItemRepository studyItemRepository;
    private final UserRepository userRepository;

    public StudyItemResponseDTO create(StudyItemRequestDTO dto, Long userId) {
        User user = userRepository.getReferenceById(userId);

        StudyItem studyItem = StudyItem.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        return mapToResponse(studyItemRepository.save(studyItem));
    }

    public StudyItemResponseDTO update(Long id, StudyItemRequestDTO dto, Long userId) {
        StudyItem item = studyItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());

        return mapToResponse(studyItemRepository.save(item));
    }

    public void delete(Long id, Long userId) {
        StudyItem item = studyItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        studyItemRepository.delete(item);
    }

    public void deleteAll(Long userId) {
        studyItemRepository.deleteAllByUserId(userId);
    }

    public List<StudyItemResponseDTO> findAllByUser(Long userId) {
        return studyItemRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
