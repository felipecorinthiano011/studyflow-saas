package com.studyflow.backend.shared.constant;

/**
 * Padrões de validação do sistema.
 */
public class ValidationPatterns {

    // Email pattern (RFC 5322 simplificado)
    public static final String EMAIL_PATTERN =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    // Senha: pelo menos 8 caracteres, 1 maiúscula, 1 minúscula, 1 número, 1 especial
    @SuppressWarnings("java:S2068") // Not a hard-coded password — this is a regex validation pattern
    public static final String PASSWORD_PATTERN =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    // Limites de campos
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;

    public static final int MIN_EMAIL_LENGTH = 5;
    public static final int MAX_EMAIL_LENGTH = 255;

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;

    public static final int MIN_TITLE_LENGTH = 3;
    public static final int MAX_TITLE_LENGTH = 100;

    public static final int MIN_DESCRIPTION_LENGTH = 0;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;

    private ValidationPatterns() {
        throw new AssertionError("Não deve ser instanciado");
    }
}

