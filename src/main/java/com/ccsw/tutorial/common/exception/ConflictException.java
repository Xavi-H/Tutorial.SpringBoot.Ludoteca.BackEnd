package com.ccsw.tutorial.common.exception;

/**
 * Gestiona les excepcions enviant el missatge al {@link GlobalExceptionHandler}
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
