package com.leilao.arremateai.exception;

/**
 * Exceção lançada quando uma operação de negócio não pode ser realizada
 * devido a regras de validação ou restrições
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
