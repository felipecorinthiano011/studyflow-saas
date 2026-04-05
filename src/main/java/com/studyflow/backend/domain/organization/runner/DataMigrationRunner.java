package com.studyflow.backend.domain.organization.runner;

import com.studyflow.backend.domain.organization.entity.Organization;
import com.studyflow.backend.domain.organization.repository.OrganizationRepository;
import com.studyflow.backend.domain.study.entity.StudyItem;
import com.studyflow.backend.domain.study.repository.StudyItemRepository;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataMigrationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataMigrationRunner.class);
    private static final String DEFAULT_ORG_NAME = "Default Organization";

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final StudyItemRepository studyItemRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Organization defaultOrg = organizationRepository.findByName(DEFAULT_ORG_NAME)
                .orElseGet(() -> {
                    logger.info("Creating default organization");
                    return organizationRepository.save(Organization.builder()
                            .name(DEFAULT_ORG_NAME)
                            .createdAt(LocalDateTime.now())
                            .build());
                });

        List<User> usersToUpdate = userRepository.findAll().stream()
                .filter(u -> u.getOrganization() == null)
                .toList();
        usersToUpdate.forEach(u -> u.setOrganization(defaultOrg));
        userRepository.saveAll(usersToUpdate);
        long usersUpdated = usersToUpdate.size();

        List<StudyItem> itemsToUpdate = studyItemRepository.findAll().stream()
                .filter(item -> item.getOrgId() == null)
                .toList();
        itemsToUpdate.forEach(item -> item.setOrgId(defaultOrg.getId()));
        studyItemRepository.saveAll(itemsToUpdate);
        long itemsUpdated = itemsToUpdate.size();

        if (usersUpdated > 0 || itemsUpdated > 0) {
            logger.info("Migration complete: {} users and {} items assigned to default org",
                    usersUpdated, itemsUpdated);
        }
    }
}

