package com.studyflow.backend.repository;

import com.studyflow.backend.entity.StudyItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyItemRepository extends JpaRepository<StudyItem, Long> {

    List<StudyItem> findByUserId(Long userId);

}