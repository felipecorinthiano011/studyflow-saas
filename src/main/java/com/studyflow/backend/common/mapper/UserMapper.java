package com.studyflow.backend.common.mapper;

import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.shared.dto.UserResponseDTO;

/**
 * Stateless mapper for User → UserResponseDTO conversions.
 * Centralises the builder call that was repeated in every UserService method.
 */
public final class UserMapper {

    private UserMapper() {
        throw new AssertionError("Utility class");
    }

    /** Maps a {@link User} entity to its DTO representation. */
    public static UserResponseDTO toDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}

