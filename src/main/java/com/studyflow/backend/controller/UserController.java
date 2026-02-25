package com.studyflow.backend.controller;

import com.studyflow.backend.dto.UserRequestDTO;
import com.studyflow.backend.dto.UserResponseDTO;
import com.studyflow.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponseDTO createUser(@RequestBody @Valid UserRequestDTO dto) {
        return userService.create(dto);
    }

    @GetMapping
    public List<UserResponseDTO> listUsers() {
        return userService.findAll();
    }
}