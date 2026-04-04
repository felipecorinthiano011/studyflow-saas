package com.studyflow.backend.domain.user.repository;

import com.studyflow.backend.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        User user = User.builder()
                .name("Test User")
                .email("test@email.com")
                .password("encodedPassword")
                .build();
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("test@email.com");

        assertTrue(found.isPresent());
        assertEquals("test@email.com", found.get().getEmail());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    void shouldReturnEmptyOptionalWhenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("notfound@email.com");

        assertFalse(found.isPresent());
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        User user = User.builder()
                .name("Another User")
                .email("another@email.com")
                .password("encodedPassword")
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("Another User", saved.getName());
        assertEquals("another@email.com", saved.getEmail());
    }

    @Test
    void shouldReturnEmptyWhenFindingByEmailCaseSensitive() {
        User user = User.builder()
                .name("Case User")
                .email("case@email.com")
                .password("encodedPassword")
                .build();
        entityManager.persistAndFlush(user);

        Optional<User> notFound = userRepository.findByEmail("CASE@EMAIL.COM");

        assertFalse(notFound.isPresent());
    }
}
