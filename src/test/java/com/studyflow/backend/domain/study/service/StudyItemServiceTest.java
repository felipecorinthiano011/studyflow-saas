package com.studyflow.backend.domain.study.service;
import com.studyflow.backend.domain.study.entity.StudyItem;
import com.studyflow.backend.domain.study.repository.StudyItemRepository;
import com.studyflow.backend.domain.audit.service.AuditLogService;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import com.studyflow.backend.shared.constant.ErrorMessages;
import com.studyflow.backend.shared.dto.StudyItemRequestDTO;
import com.studyflow.backend.shared.dto.StudyItemResponseDTO;
import com.studyflow.backend.shared.exception.DomainAccessDeniedException;
import com.studyflow.backend.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StudyItemServiceTest {

    @Mock private StudyItemRepository studyItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private AuditLogService auditLogService;
    @InjectMocks private StudyItemService studyItemService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .id(1L).name("Test User")
                .email("test@email.com").password("encoded")
                .build();
    }

    @Test
    void shouldCreateStudyItemSuccessfully() {
        StudyItemRequestDTO dto = StudyItemRequestDTO.builder()
                .title("Learn Spring Boot").description("Study JWT authentication").build();
        StudyItem saved = StudyItem.builder()
                .id(1L).title("Learn Spring Boot").description("Study JWT authentication")
                .createdAt(LocalDateTime.now()).user(testUser).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studyItemRepository.save(any(StudyItem.class))).thenReturn(saved);

        StudyItemResponseDTO result = studyItemService.create(dto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Learn Spring Boot", result.getTitle());
        assertEquals("Study JWT authentication", result.getDescription());
        assertNotNull(result.getCreatedAt());
        verify(userRepository).findById(1L);
        verify(studyItemRepository).save(any(StudyItem.class));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void shouldUpdateStudyItemSuccessfully() {
        StudyItemRequestDTO dto = StudyItemRequestDTO.builder()
                .title("Updated Title").description("Updated Description").build();
        StudyItem existing = StudyItem.builder()
                .id(1L).title("Old Title").description("Old Description")
                .createdAt(LocalDateTime.now()).user(testUser).build();
        StudyItem updated = StudyItem.builder()
                .id(1L).title("Updated Title").description("Updated Description")
                .createdAt(existing.getCreatedAt()).user(testUser).build();

        when(studyItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studyItemRepository.save(any(StudyItem.class))).thenReturn(updated);

        StudyItemResponseDTO result = studyItemService.update(1L, dto, 1L);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        verify(studyItemRepository).findById(1L);
        verify(studyItemRepository).save(any(StudyItem.class));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentItem() {
        StudyItemRequestDTO dto = StudyItemRequestDTO.builder().title("Title").build();
        when(studyItemRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> studyItemService.update(99L, dto, 1L));
        assertEquals(ErrorMessages.STUDY_ITEM_NOT_FOUND, ex.getMessage());
        verify(studyItemRepository, never()).save(any());
    }

    @Test
    void shouldThrowAccessDeniedWhenUpdatingOtherUsersItem() {
        User other = User.builder().id(2L).name("Other").email("other@email.com").password("encoded").build();
        StudyItem item = StudyItem.builder().id(1L).title("Title").user(other).build();
        when(studyItemRepository.findById(1L)).thenReturn(Optional.of(item));

        DomainAccessDeniedException ex = assertThrows(DomainAccessDeniedException.class,
                () -> studyItemService.update(1L, StudyItemRequestDTO.builder().title("New").build(), 1L));
        assertEquals(ErrorMessages.ACCESS_DENIED, ex.getMessage());
        verify(studyItemRepository, never()).save(any());
    }

    @Test
    void shouldDeleteStudyItemSuccessfully() {
        StudyItem item = StudyItem.builder().id(1L).title("Title").user(testUser).build();
        when(studyItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertDoesNotThrow(() -> studyItemService.delete(1L, 1L));
        verify(studyItemRepository).findById(1L);
        verify(studyItemRepository).delete(item);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void shouldThrowWhenDeletingNonExistentItem() {
        when(studyItemRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> studyItemService.delete(99L, 1L));
        assertEquals(ErrorMessages.STUDY_ITEM_NOT_FOUND, ex.getMessage());
        verify(studyItemRepository, never()).delete(any());
    }

    @Test
    void shouldThrowAccessDeniedWhenDeletingOtherUsersItem() {
        User other = User.builder().id(2L).name("Other").email("other@email.com").password("encoded").build();
        StudyItem item = StudyItem.builder().id(1L).title("Title").user(other).build();
        when(studyItemRepository.findById(1L)).thenReturn(Optional.of(item));

        DomainAccessDeniedException ex = assertThrows(DomainAccessDeniedException.class,
                () -> studyItemService.delete(1L, 1L));
        assertEquals(ErrorMessages.ACCESS_DENIED, ex.getMessage());
        verify(studyItemRepository, never()).delete(any());
    }

    @Test
    void shouldReturnAllStudyItemsForUser() {
        StudyItem item1 = StudyItem.builder().id(1L).title("Item 1").description("Description 1")
                .createdAt(LocalDateTime.now()).user(testUser).build();
        StudyItem item2 = StudyItem.builder().id(2L).title("Item 2").description("Description 2")
                .createdAt(LocalDateTime.now()).user(testUser).build();
        Pageable pageable = PageRequest.of(0, 20);
        when(studyItemRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item1, item2)));

        Page<StudyItemResponseDTO> result = studyItemService.findAllByUser(1L, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Item 1", result.getContent().get(0).getTitle());
        assertEquals("Item 2", result.getContent().get(1).getTitle());
        verify(studyItemRepository).findByUserId(eq(1L), any(Pageable.class));
    }

    @Test
    void shouldReturnEmptyPageWhenUserHasNoStudyItems() {
        Pageable pageable = PageRequest.of(0, 20);
        when(studyItemRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<StudyItemResponseDTO> result = studyItemService.findAllByUser(1L, pageable);

        assertTrue(result.isEmpty());
        verify(studyItemRepository).findByUserId(eq(1L), any(Pageable.class));
    }
}