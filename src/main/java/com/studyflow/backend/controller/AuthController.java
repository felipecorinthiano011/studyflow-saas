package com.studyflow.backend.controller;

import com.studyflow.backend.dto.LoginRequest;
import com.studyflow.backend.entity.User;
import com.studyflow.backend.repository.UserRepository;
import com.studyflow.backend.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticação e geração de token JWT")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Operation(
        summary = "Login",
        description = "Autentica o usuário e retorna um token JWT válido por 1 hora.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                {
                  "email": "usuario@email.com",
                  "password": "suasenha"
                }
                """))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                content = @Content(schema = @Schema(example = """
                    {"token": "eyJhbGciOiJIUzI1NiJ9..."}
                    """))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
        }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Senha inválida");
        }

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(Map.of("token", token));
    }
}
