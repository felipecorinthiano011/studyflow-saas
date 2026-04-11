package com.studyflow.backend.regression;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.studyflow.backend.shared.dto.PageResponseDTO;
import com.studyflow.backend.shared.dto.StudyItemResponseDTO;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for the Redis cache serialization fix.
 *
 * <p>Root cause of the original bug: {@code PageResponseDTO} and
 * {@code StudyItemResponseDTO} did not implement {@link java.io.Serializable}, so Redis
 * (using the JDK serializer) threw a {@link java.io.NotSerializableException} on every
 * cache read (i.e. the second GET request, simulating a page refresh), producing HTTP 500.
 *
 * <p>These pure unit tests guard against reverting the {@code implements Serializable} change
 * by verifying that both DTOs survive a full JDK and Jackson serialization roundtrip.
 */
class StudyItemDtoSerializationRegressionTest {

    // ── Fixtures ─────────────────────────────────────────────────────────────

    private static StudyItemResponseDTO sampleItem() {
        return StudyItemResponseDTO.builder()
                .id(42L)
                .title("Study Spring Boot")
                .description("Deep dive into caching")
                .createdAt(LocalDateTime.of(2024, 6, 15, 10, 30))
                .build();
    }

    private static PageResponseDTO<StudyItemResponseDTO> samplePage() {
        return PageResponseDTO.<StudyItemResponseDTO>builder()
                .content(List.of(sampleItem()))
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();
    }

    // ── JDK serialization (Redis default / JdkSerializationRedisSerializer) ──

    @Test
    void studyItemResponseDTO_shouldSurviveJdkSerializationRoundtrip() throws Exception {
        StudyItemResponseDTO original = sampleItem();

        StudyItemResponseDTO restored = jdkRoundtrip(original, StudyItemResponseDTO.class);

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getTitle(), restored.getTitle());
        assertEquals(original.getDescription(), restored.getDescription());
        assertEquals(original.getCreatedAt(), restored.getCreatedAt());
    }

    @Test
    void pageResponseDTO_shouldSurviveJdkSerializationRoundtrip() throws Exception {
        PageResponseDTO<StudyItemResponseDTO> original = samplePage();

        @SuppressWarnings("unchecked")
        PageResponseDTO<StudyItemResponseDTO> restored =
                jdkRoundtrip(original, PageResponseDTO.class);

        assertEquals(original.getTotalElements(), restored.getTotalElements());
        assertEquals(original.getTotalPages(), restored.getTotalPages());
        assertEquals(original.getPage(), restored.getPage());
        assertEquals(original.getSize(), restored.getSize());
        assertTrue(restored.isLast());
        assertEquals(1, restored.getContent().size());
    }

    @Test
    void pageResponseDTO_contentItemsShouldSurviveJdkSerializationRoundtrip() throws Exception {
        PageResponseDTO<StudyItemResponseDTO> original = samplePage();

        @SuppressWarnings("unchecked")
        PageResponseDTO<StudyItemResponseDTO> restored =
                jdkRoundtrip(original, PageResponseDTO.class);

        StudyItemResponseDTO item = (StudyItemResponseDTO) restored.getContent().get(0);
        assertEquals("Study Spring Boot", item.getTitle());
        assertEquals("Deep dive into caching", item.getDescription());
        assertEquals(42L, item.getId());
    }

    // ── Jackson serialization (GenericJackson2JsonRedisSerializer) ────────────

    @Test
    void studyItemResponseDTO_shouldSurviveJacksonSerializationRoundtrip() throws Exception {
        ObjectMapper mapper = jacksonMapper();
        StudyItemResponseDTO original = sampleItem();

        String json = mapper.writeValueAsString(original);
        StudyItemResponseDTO restored = mapper.readValue(json, StudyItemResponseDTO.class);

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getTitle(), restored.getTitle());
        assertEquals(original.getDescription(), restored.getDescription());
    }

    @Test
    void pageResponseDTO_shouldSurviveJacksonSerializationRoundtrip() throws Exception {
        ObjectMapper mapper = jacksonMapper();
        PageResponseDTO<StudyItemResponseDTO> original = samplePage();

        String json = mapper.writeValueAsString(original);

        @SuppressWarnings("unchecked")
        PageResponseDTO<StudyItemResponseDTO> restored =
                mapper.readValue(json, PageResponseDTO.class);

        assertEquals(original.getTotalElements(), restored.getTotalElements());
        assertEquals(original.getPage(), restored.getPage());
        assertEquals(original.getSize(), restored.getSize());
        assertNotNull(restored.getContent());
    }

    // ── serialVersionUID presence (stability guard) ───────────────────────────

    @Test
    void studyItemResponseDTO_shouldDeclareSerialVersionUID() throws NoSuchFieldException {
        var field = StudyItemResponseDTO.class.getDeclaredField("serialVersionUID");
        assertNotNull(field, "serialVersionUID must be declared for stable JDK serialization");
    }

    @Test
    void pageResponseDTO_shouldDeclareSerialVersionUID() throws NoSuchFieldException {
        var field = PageResponseDTO.class.getDeclaredField("serialVersionUID");
        assertNotNull(field, "serialVersionUID must be declared for stable JDK serialization");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private static <T> T jdkRoundtrip(T obj, Class<T> type)
            throws IOException, ClassNotFoundException {
        byte[] bytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            bytes = baos.toByteArray();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return type.cast(ois.readObject());
        }
    }

    private static ObjectMapper jacksonMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
