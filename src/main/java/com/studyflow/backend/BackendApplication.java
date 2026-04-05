package com.studyflow.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Aplicação principal do StudyFlow Backend.
 *
 * Configurações:
 * - Repositories em: com.studyflow.backend.domain.*.repository
 * - Entities em: com.studyflow.backend.domain.*.entity
 * - Transações habilitadas para operações em banco
 * - Cache habilitado (simple em dev, Redis em prod)
 * - Async habilitado para processamento de eventos
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@EnableAsync
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}

