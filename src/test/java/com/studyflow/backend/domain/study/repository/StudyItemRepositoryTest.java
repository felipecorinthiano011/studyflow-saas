package com.studyflow.backend.domain.study.repository;

import com.studyflow.backend.domain.study.entity.StudyItem;
import com.studyflow.backend.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
class StudyItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudyItemRepository studyItemRepository;

    private User createAndPersistUser(String name, String email) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password("encoded")
                .build();
        return entityManager.persistAndFlush(user);
    }

    @Test
    void shouldFindStudyItemsByUserId() {
        User user = createAndPersistUser("Test User", "test@email.com");

        StudyItem item1 = StudyItem.builder()
                .title("Item 1")
                .description("Description 1")
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        StudyItem item2 = StudyItem.builder()
                .title("Item 2")
                .description("Description 2")
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        entityManager.persistAndFlush(item1);
        entityManager.persistAndFlush(item2);

        List<StudyItem> items = studyItemRepository.findByUserId(user.getId());

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getTitle().equals("Item 1")));
        assertTrue(items.stream().anyMatch(i -> i.getTitle().equals("Item 2")));
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoStudyItems() {
        User user = createAndPersistUser("Empty User", "empty@email.com");

        List<StudyItem> items = studyItemRepository.findByUserId(user.getId());

        assertTrue(items.isEmpty());
    }

    @Test
    void shouldOnlyReturnItemsForSpecificUser() {
        User user1 = createAndPersistUser("User 1", "user1@email.com");
        User user2 = createAndPersistUser("User 2", "user2@email.com");

        entityManager.persistAndFlush(StudyItem.builder()
                .title("User1 Item")
                .createdAt(LocalDateTime.now())
                .user(user1)
                .build());

        entityManager.persistAndFlush(StudyItem.builder()
                .title("User2 Item")
                .createdAt(LocalDateTime.now())
                .user(user2)
                .build());

        List<StudyItem> user1Items = studyItemRepository.findByUserId(user1.getId());

        assertEquals(1, user1Items.size());
        assertEquals("User1 Item", user1Items.get(0).getTitle());
    }

    @Test
    void shouldReturnEmptyListForNonExistentUserId() {
        List<StudyItem> items = studyItemRepository.findByUserId(9999L);

        assertTrue(items.isEmpty());
    }
}
