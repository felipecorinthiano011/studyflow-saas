package com.studyflow.backend.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para criação de um item de estudo")
public class StudyItemRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must have at most 255 characters")
    @Schema(description = "Título do item de estudo", example = "Estudar Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Size(max = 2000, message = "Description must have at most 2000 characters")
    @Schema(description = "Descrição detalhada", example = "Revisar autenticação JWT e filtros de segurança")
    private String description;
}
