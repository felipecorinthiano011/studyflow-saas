package com.studyflow.backend.controller;

import com.studyflow.backend.dto.LoginRequest;
import com.studyflow.backend.entity.User;
import com.studyflow.backend.repository.UserRepository;
import com.studyflow.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

        return ResponseEntity.ok(token);
    }
}