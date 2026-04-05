package com.studyflow.backend.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import com.studyflow.backend.shared.constant.ValidationPatterns;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para criação de um item de estudo")
public class StudyItemRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = ValidationPatterns.MAX_TITLE_LENGTH, message = "Title must have at most {max} characters")
    @Schema(description = "Título do item de estudo", example = "Estudar Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Size(max = ValidationPatterns.MAX_DESCRIPTION_LENGTH, message = "Description must have at most {max} characters")
    @Schema(description = "Descrição detalhada", example = "Revisar autenticação JWT e filtros de segurança")
    private String description;
}
