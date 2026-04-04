package com.studyflow.backend.shared.constant;

/**
 * Mensagens de erro padrão do sistema.
 */
public class ErrorMessages {
    public static final String USER_NOT_FOUND = "Usuário não encontrado";
    public static final String USER_ALREADY_EXISTS = "Usuário já existe";
    public static final String INVALID_EMAIL = "Email inválido";
    public static final String INVALID_PASSWORD = "Senha não atende aos requisitos";
    public static final String UNAUTHORIZED = "Acesso não autorizado";
    public static final String INVALID_CREDENTIALS = "Email ou senha incorretos";
    public static final String STUDY_ITEM_NOT_FOUND = "Item de estudo não encontrado";
    public static final String ACCESS_DENIED = "Acesso negado";
    public static final String TOKEN_INVALID = "Token inválido";
    public static final String TOKEN_EXPIRED = "Token expirado";

    private ErrorMessages() {
        throw new AssertionError("Não deve ser instanciado");
    }
}

