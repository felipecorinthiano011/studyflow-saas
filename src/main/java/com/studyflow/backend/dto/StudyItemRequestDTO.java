package com.studyflow.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyItemRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must have at most 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must have at most 2000 characters")
    private String description;

}