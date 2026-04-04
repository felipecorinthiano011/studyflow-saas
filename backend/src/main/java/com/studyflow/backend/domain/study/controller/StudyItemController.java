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
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Criar item de estudo",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                {"title": "Estudar Spring Boot", "description": "Revisar JWT"}
                """))),
        responses = {
            @ApiResponse(responseCode = "200", description = "Item criado"),
            @ApiResponse(responseCode = "400", description = "Título inválido"),
            @ApiResponse(responseCode = "403", description = "Não autenticado")
        })
    @PostMapping
    public StudyItemResponseDTO create(@Valid @RequestBody StudyItemRequestDTO dto, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return studyItemService.create(dto, user.getId());
    }

    @Operation(summary = "Listar itens de estudo",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "403", description = "Não autenticado")
        })
    @GetMapping
    public List<StudyItemResponseDTO> getAll(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return studyItemService.findAllByUser(user.getId());
    }

    @Operation(summary = "Atualizar item de estudo",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                {"title": "Título atualizado", "description": "Nova descrição"}
                """))),
        responses = {
            @ApiResponse(responseCode = "200", description = "Item atualizado"),
            @ApiResponse(responseCode = "400", description = "Item não encontrado ou acesso negado"),
            @ApiResponse(responseCode = "403", description = "Não autenticado")
        })
    @PutMapping("/{id}")
    public StudyItemResponseDTO update(@PathVariable Long id, @Valid @RequestBody StudyItemRequestDTO dto,
                                       Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return studyItemService.update(id, dto, user.getId());
    }

    @Operation(summary = "Excluir item de estudo",
        responses = {
            @ApiResponse(responseCode = "204", description = "Item excluído"),
            @ApiResponse(responseCode = "400", description = "Item não encontrado ou acesso negado"),
            @ApiResponse(responseCode = "403", description = "Não autenticado")
        })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        studyItemService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    private User getAuthenticatedUser(Authentication authentication) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
