package com.studyflow.backend.regression;

import com.studyflow.backend.domain.study.repository.StudyItemRepository;
import com.studyflow.backend.domain.study.service.StudyItemService;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import com.studyflow.backend.shared.dto.PageResponseDTO;
import com.studyflow.backend.shared.dto.StudyItemRequestDTO;
import com.studyflow.backend.shared.dto.StudyItemResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Regression tests for the in-process cache layer introduced to avoid hitting the database
 * on every request.
 *
 * <p>Uses {@code spring.cache.type=simple} (in-memory {@code ConcurrentMapCacheManager}) so
 * the {@code @Cacheable} / {@code @CacheEvict} annotations are active, independently of
 * Redis availability.  A {@code @SpyBean} on {@link StudyItemRepository} lets us count
 * actual database calls and verify that:
 * <ul>
 *   <li>A second read within the same cache epoch hits the cache (zero extra DB calls).</li>
 *   <li>Every mutating operation (create / update / delete / deleteAll) evicts the cache so
 *       the next read reflects the latest state from the database.</li>
 * </ul>
 *
 * <p>These tests guard against:
 * <ul>
 *   <li>Reverting the service method return type back to {@code Page<>}, which cannot be
 *       cached reliably.</li>
 *   <li>Accidentally removing {@code @CacheEvict} from a mutating operation, causing stale
 *       data to be served after a write.</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.cache.type=simple")
class StudyItemCacheBehaviorRegressionTest {

    @Autowired
    private StudyItemService studyItemService;

    @SpyBean
    private StudyItemRepository studyItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    private User testUser;

    private static final Pageable PAGE =
            PageRequest.of(0, 20, Sort.by("createdAt").descending());

    @BeforeEach
    void setUp() {
        studyItemRepository.deleteAll();
        userRepository.deleteAll();
        clearAllCaches();
        Mockito.clearInvocations(studyItemRepository);

        testUser = userRepository.save(User.builder()
                .name("Cache Regression User")
                .email("cache.regression@test.com")
                .password("encoded-pw")
                .build());
    }

    @AfterEach
    void tearDown() {
        studyItemRepository.deleteAll();
        userRepository.deleteAll();
        clearAllCaches();
    }

    // ── Scenario 1: repeated reads must not cause 500 ────────────────────────

    @Test
    void repeatedReadsReturnConsistentResultsWithoutError() {
        PageResponseDTO<StudyItemResponseDTO> first =
                studyItemService.findAllByUser(testUser.getId(), PAGE);
        PageResponseDTO<StudyItemResponseDTO> second =
                studyItemService.findAllByUser(testUser.getId(), PAGE);

        assertNotNull(first, "First call must return a non-null result");
        assertNotNull(second, "Second call (simulated page refresh) must return a non-null result");
        assertEquals(first.getTotalElements(), second.getTotalElements());
        assertEquals(first.getContent().size(), second.getContent().size());
    }

    // ── Scenario 2: second read must be served from cache (no extra DB call) ─

    @Test
    void secondReadHitsCacheAndDoesNotQueryDatabase() {
        studyItemService.findAllByUser(testUser.getId(), PAGE); // cache miss → DB
        studyItemService.findAllByUser(testUser.getId(), PAGE); // cache hit → no extra DB call

        verify(studyItemRepository, times(1))
                .findByUserId(eq(testUser.getId()), any(Pageable.class));
    }

    // ── Scenario 3: cache evicted after create ───────────────────────────────

    @Test
    void createEvictsCacheAndNextReadReflectsNewItem() {
        studyItemService.findAllByUser(testUser.getId(), PAGE); // populate cache

        studyItemService.create(
                StudyItemRequestDTO.builder().title("New Item").description("Created").build(),
                testUser.getId()); // evicts cache

        PageResponseDTO<StudyItemResponseDTO> result =
                studyItemService.findAllByUser(testUser.getId(), PAGE); // cache miss → DB

        // Repository must have been queried twice: before and after create
        verify(studyItemRepository, times(2))
                .findByUserId(eq(testUser.getId()), any(Pageable.class));
        assertEquals(1, result.getTotalElements(),
                "Newly created item must appear after cache eviction");
    }

    // ── Scenario 4: cache evicted after update ───────────────────────────────

    @Test
    void updateEvictsCacheAndNextReadReflectsUpdatedTitle() {
        StudyItemResponseDTO created = studyItemService.create(
                StudyItemRequestDTO.builder().title("Original").description("Before").build(),
                testUser.getId());

        Mockito.clearInvocations(studyItemRepository);
        studyItemService.findAllByUser(testUser.getId(), PAGE); // populate cache

        studyItemService.update(created.getId(),
                StudyItemRequestDTO.builder().title("Updated").description("After").build(),
                testUser.getId()); // evicts cache

        PageResponseDTO<StudyItemResponseDTO> result =
                studyItemService.findAllByUser(testUser.getId(), PAGE); // cache miss → DB

        verify(studyItemRepository, times(2))
                .findByUserId(eq(testUser.getId()), any(Pageable.class));
        assertEquals("Updated", result.getContent().get(0).getTitle());
    }

    // ── Scenario 5: cache evicted after delete ───────────────────────────────

    @Test
    void deleteEvictsCacheAndNextReadReflectsRemoval() {
        StudyItemResponseDTO created = studyItemService.create(
                StudyItemRequestDTO.builder().title("To Delete").build(),
                testUser.getId());

        Mockito.clearInvocations(studyItemRepository);
        studyItemService.findAllByUser(testUser.getId(), PAGE); // populate cache

        studyItemService.delete(created.getId(), testUser.getId()); // evicts cache

        PageResponseDTO<StudyItemResponseDTO> result =
                studyItemService.findAllByUser(testUser.getId(), PAGE); // cache miss → DB

        verify(studyItemRepository, times(2))
                .findByUserId(eq(testUser.getId()), any(Pageable.class));
        assertEquals(0, result.getTotalElements(),
                "Deleted item must not appear after cache eviction");
    }

    // ── Scenario 6: cache evicted after deleteAll ────────────────────────────

    @Test
    void deleteAllEvictsCacheAndNextReadReturnsEmptyList() {
        studyItemService.create(StudyItemRequestDTO.builder().title("A").build(), testUser.getId());
        studyItemService.create(StudyItemRequestDTO.builder().title("B").build(), testUser.getId());

        Mockito.clearInvocations(studyItemRepository);
        studyItemService.findAllByUser(testUser.getId(), PAGE); // populate cache

        studyItemService.deleteAll(testUser.getId()); // evicts cache

        PageResponseDTO<StudyItemResponseDTO> result =
                studyItemService.findAllByUser(testUser.getId(), PAGE); // cache miss → DB

        verify(studyItemRepository, times(2))
                .findByUserId(eq(testUser.getId()), any(Pageable.class));
        assertTrue(result.getContent().isEmpty(),
                "All items must be gone after deleteAll + cache eviction");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void clearAllCaches() {
        cacheManager.getCacheNames().stream()
                .map(cacheManager::getCache)
                .filter(Objects::nonNull)
                .forEach(org.springframework.cache.Cache::clear);
    }
}
