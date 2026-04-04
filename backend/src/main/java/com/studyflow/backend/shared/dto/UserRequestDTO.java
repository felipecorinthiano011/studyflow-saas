package com.studyflow.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Dados para cadastro de usuário")
public class UserRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome completo", example = "João Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    @Schema(description = "Email do usuário", example = "joao@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha de acesso", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
