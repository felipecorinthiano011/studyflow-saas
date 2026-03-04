package com.studyflow.backend.controller;

import com.studyflow.backend.dto.StudyItemRequestDTO;
import com.studyflow.backend.dto.StudyItemResponseDTO;
import com.studyflow.backend.entity.User;
import com.studyflow.backend.repository.UserRepository;
import com.studyflow.backend.service.StudyItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/study-items")
@RequiredArgsConstructor
public class StudyItemController {

    private final StudyItemService studyItemService;
    private final UserRepository userRepository;

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

    @GetMapping
    public List<StudyItemResponseDTO> getAll(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return studyItemService.findAllByUser(user.getId());
    }
}