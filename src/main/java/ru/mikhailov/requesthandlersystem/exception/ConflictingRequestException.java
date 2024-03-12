package ru.mikhailov.requesthandlersystem.exception;

public class ConflictingRequestException extends RuntimeException {
    public ConflictingRequestException(String message) {
        super(message);
    }
}
