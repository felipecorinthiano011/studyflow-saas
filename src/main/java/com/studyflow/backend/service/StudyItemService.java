package com.studyflow.backend.service;

import com.studyflow.backend.dto.StudyItemRequestDTO;
import com.studyflow.backend.dto.StudyItemResponseDTO;
import com.studyflow.backend.entity.StudyItem;
import com.studyflow.backend.entity.User;
import com.studyflow.backend.repository.StudyItemRepository;
import com.studyflow.backend.repository.UserRepository;
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudyItem studyItem = StudyItem.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        StudyItem saved = studyItemRepository.save(studyItem);

        return mapToResponse(saved);
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