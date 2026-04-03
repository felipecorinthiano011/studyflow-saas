package com.studyflow.backend.controller;

import com.studyflow.backend.dto.StudyItemRequestDTO;
import com.studyflow.backend.dto.StudyItemResponseDTO;
import com.studyflow.backend.entity.User;
import com.studyflow.backend.repository.UserRepository;
import com.studyflow.backend.service.StudyItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/study-items")
@RequiredArgsConstructor
@Tag(name = "Study Items", description = "Gerenciamento de itens de estudo do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class StudyItemController {

    private final StudyItemService studyItemService;
    private final UserRepository userRepository;

    @Operation(
        summary = "Criar item de estudo",
        description = "Cria um novo item de estudo vinculado ao usuário autenticado.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                {
                  "title": "Estudar Spring Boot",
                  "description": "Revisar autenticação JWT e filtros de segurança"
                }
                """))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Item criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Título ausente ou inválido",
                content = @Content(examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "errors": { "title": "Title is required" }
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Token ausente ou inválido")
        }
    )
    @PostMapping
    public StudyItemResponseDTO create(
            @Valid @RequestBody StudyItemRequestDTO dto,
            Authentication authentication
    ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return studyItemService.create(dto, user.getId());
    }

    @Operation(
        summary = "Listar itens de estudo",
        description = "Retorna todos os itens de estudo do usuário autenticado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token ausente ou inválido")
        }
    )
    @GetMapping
    public List<StudyItemResponseDTO> getAll(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return studyItemService.findAllByUser(user.getId());
    }
}
