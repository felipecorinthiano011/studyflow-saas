package com.studyflow.backend.domain.study.service;

import com.studyflow.backend.domain.study.entity.StudyItem;
import com.studyflow.backend.domain.study.repository.StudyItemRepository;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import com.studyflow.backend.shared.dto.StudyItemRequestDTO;
import com.studyflow.backend.shared.dto.StudyItemResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StudyItemServiceTest {

    @Mock
    private StudyItemRepository studyItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StudyItemService studyItemService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .password("encoded")
                .build();
    }

    @Test
    void shouldCreateStudyItemSuccessfully() {
        StudyItemRequestDTO dto = StudyItemRequestDTO.builder()
                .title("Learn Spring Boot")
                .description("Study JWT authentication")
                .build();

        StudyItem saved = StudyItem.builder()
                .id(1L)
                .title("Learn Spring Boot")
                .description("Study JWT authentication")
                .createdAt(LocalDateTime.now())
                .user(testUser)
                .build();

        when(userRepository.getReferenceById(1L)).thenReturn(testUser);
        when(studyItemRepository.save(any(StudyItem.class))).thenReturn(saved);

        StudyItemResponseDTO result = studyItemService.create(dto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Learn Spring Boot", result.getTitle());
        assertEquals("Study JWT authentication", result.getDescription());
        assertNotNull(result.getCreatedAt());
        verify(userRepository).getReferenceById(1L);
        verify(studyItemRepository).save(any(StudyItem.class));
    }

    @Test
    void shouldUpdateStudyItemSuccessfully() {
        StudyItemRequestDTO dto = StudyItemRequestDTO.builder()
                .title("Updated Title")
                .description("Updated Description")
                .build();

        StudyItem existingItem = StudyItem.builder()
                .id(1L)
                .title("Old Title")
                .description("Old Description")
                .createdAt(LocalDateTime.now())
                .user(testUser)
                .build();

        StudyItem updatedItem = StudyItem.builder()
                .id(1L)
                .title("Updated Title")
                .description("Updated Description")
                .createdAt(existingItem.getCreatedAt())
                .user(testUser)
                .build();

        when(studyItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(studyItemRepository.save(any(StudyItem.class))).thenReturn(updatedItem);

        StudyItemResponseDTO result = studyItemService.update(1L, dto, 1L);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        verify(studyItemRepository).findById(1L);
        verify(studyItemRepository).save(any(StudyItem.class));
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentItem() {
        StudyItemRequestDTO dto = StudyItemRequestDTO.builder()
                .title("Title")
                .build();

        when(studyItemRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> studyItemService.update(99L, dto, 1L));

        assertEquals("Item not found", ex.getMessage());
        verify(studyItemRepository).findById(99L);
        verify(studyItemRepository, never()).save(any());
    }

    @Test
    void shouldThrowAccessDeniedWhenUpdatingOtherUsersItem() {
        User anotherUser = User.builder()
                .id(2L)
                .name("Another User")
                .email("another@email.com")
                .password("encoded")
                .build();

        StudyItem existingItem = StudyItem.builder()
                .id(1L)
                .title("Title")
                .user(anotherUser)
                .build();

        when(studyItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> studyItemService.update(1L, StudyItemRequestDTO.builder().title("New").build(), 1L));

        assertEquals("Access denied", ex.getMessage());
        verify(studyItemRepository, never()).save(any());
    }

    @Test
    void shouldDeleteStudyItemSuccessfully() {
        StudyItem existingItem = StudyItem.builder()
                .id(1L)
                .title("Title")
                .user(testUser)
                .build();

        when(studyItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        assertDoesNotThrow(() -> studyItemService.delete(1L, 1L));

        verify(studyItemRepository).findById(1L);
        verify(studyItemRepository).delete(existingItem);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentItem() {
        when(studyItemRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> studyItemService.delete(99L, 1L));

        assertEquals("Item not found", ex.getMessage());
        verify(studyItemRepository, never()).delete(any());
    }

    @Test
    void shouldThrowAccessDeniedWhenDeletingOtherUsersItem() {
        User anotherUser = User.builder()
                .id(2L)
                .name("Another User")
                .email("another@email.com")
                .password("encoded")
                .build();

        StudyItem existingItem = StudyItem.builder()
                .id(1L)
                .title("Title")
                .user(anotherUser)
                .build();

        when(studyItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> studyItemService.delete(1L, 1L));

        assertEquals("Access denied", ex.getMessage());
        verify(studyItemRepository, never()).delete(any());
    }

    @Test
    void shouldReturnAllStudyItemsForUser() {
        StudyItem item1 = StudyItem.builder()
                .id(1L)
                .title("Item 1")
                .description("Description 1")
                .createdAt(LocalDateTime.now())
                .user(testUser)
                .build();

        StudyItem item2 = StudyItem.builder()
                .id(2L)
                .title("Item 2")
                .description("Description 2")
                .createdAt(LocalDateTime.now())
                .user(testUser)
                .build();

        when(studyItemRepository.findByUserId(1L)).thenReturn(List.of(item1, item2));

        List<StudyItemResponseDTO> result = studyItemService.findAllByUser(1L);

        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getTitle());
        assertEquals("Item 2", result.get(1).getTitle());
        verify(studyItemRepository).findByUserId(1L);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoStudyItems() {
        when(studyItemRepository.findByUserId(1L)).thenReturn(List.of());

        List<StudyItemResponseDTO> result = studyItemService.findAllByUser(1L);

        assertTrue(result.isEmpty());
        verify(studyItemRepository).findByUserId(1L);
    }
}
