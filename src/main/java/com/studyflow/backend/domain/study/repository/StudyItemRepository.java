package com.studyflow.backend.domain.study.repository;

import com.studyflow.backend.domain.study.entity.StudyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StudyItemRepository extends JpaRepository<StudyItem, Long> {

    List<StudyItem> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudyItem s WHERE s.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

}