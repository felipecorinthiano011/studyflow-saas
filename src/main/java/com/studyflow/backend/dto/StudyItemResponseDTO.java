package com.studyflow.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyItemResponseDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;

}