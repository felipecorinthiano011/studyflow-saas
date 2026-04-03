package com.studyflow.backend.controller;

import com.studyflow.backend.dto.UserRequestDTO;
import com.studyflow.backend.dto.UserResponseDTO;
import com.studyflow.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Cadastro e listagem de usuários")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Criar usuário",
        description = "Cadastra um novo usuário. Não requer autenticação.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                {
                  "name": "João Silva",
                  "email": "joao@email.com",
                  "password": "senha123"
                }
                """))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já cadastrado",
                content = @Content(examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "errors": { "email": "Email inválido" }
                    }
                    """)))
        }
    )
    @PostMapping
    public UserResponseDTO createUser(@RequestBody @Valid UserRequestDTO dto) {
        return userService.create(dto);
    }

    @Operation(
        summary = "Listar usuários",
        description = "Retorna todos os usuários cadastrados. Requer autenticação.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token ausente ou inválido")
        }
    )
    @GetMapping
    public List<UserResponseDTO> listUsers() {
        return userService.findAll();
    }
}
