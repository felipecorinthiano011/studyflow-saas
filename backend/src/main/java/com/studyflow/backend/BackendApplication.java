package com.studyflow.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Aplicação principal do StudyFlow Backend.
 *
 * Configurações:
 * - Repositories em: com.studyflow.backend.domain.*.repository
 * - Entities em: com.studyflow.backend.domain.*.entity
 * - Transações habilitadas para operações em banco
 */
@SpringBootApplication(scanBasePackages = {
    "com.studyflow.backend.domain",
    "com.studyflow.backend.shared",
    "com.studyflow.backend.common",
    "com.studyflow.backend.security",
    "com.studyflow.backend.config"
})
@EnableJpaRepositories(basePackages = {
    "com.studyflow.backend.domain.user.repository",
    "com.studyflow.backend.domain.study.repository"
})
@EnableTransactionManagement
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}


