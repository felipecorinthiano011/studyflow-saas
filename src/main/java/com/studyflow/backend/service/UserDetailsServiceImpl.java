package com.studyflow.backend.service;

import com.studyflow.backend.domain.user.entity.Role;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Build authorities from the user's assigned role (e.g. ROLE_USER, ROLE_ADMIN).
        // Default to USER if role is null (e.g. legacy rows created before the column was added).
        Role role = user.getRole() != null ? user.getRole() : Role.USER;
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
    }
}