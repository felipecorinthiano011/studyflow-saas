package com.studyflow.backend.domain.user.service;

import com.studyflow.backend.common.mapper.UserMapper;
import com.studyflow.backend.domain.audit.service.AuditLogService;
import com.studyflow.backend.shared.constant.ErrorMessages;
import com.studyflow.backend.shared.dto.UserRequestDTO;
import com.studyflow.backend.shared.dto.UserResponseDTO;
import com.studyflow.backend.domain.organization.repository.OrganizationRepository;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import com.studyflow.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;

    public UserResponseDTO create(UserRequestDTO dto) {
        logger.info("Creating new user");

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .organization(organizationRepository.findByName("Default Organization").orElse(null))
                .build();

        User saved = userRepository.save(user);
        logger.info("User created successfully with ID: {}", saved.getId());
        auditLogService.logAction(saved.getId(), "REGISTER", "User", saved.getId(),
                "New user registered");
        return UserMapper.toDTO(saved);
    }

    public List<UserResponseDTO> findAll() {
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();
        logger.info("Found {} users", users.size());
        return users.stream().map(UserMapper::toDTO).toList();
    }

    public UserResponseDTO findByEmail(String email) {
        logger.info("Finding user by email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));
        return UserMapper.toDTO(user);
    }
}
