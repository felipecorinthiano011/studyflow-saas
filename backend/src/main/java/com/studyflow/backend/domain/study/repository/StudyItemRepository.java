package com.studyflow.backend.domain.study.repository;

import com.studyflow.backend.domain.study.entity.StudyItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyItemRepository extends JpaRepository<StudyItem, Long> {

    List<StudyItem> findByUserId(Long userId);

}