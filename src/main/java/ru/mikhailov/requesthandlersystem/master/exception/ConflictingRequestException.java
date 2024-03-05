package ru.mikhailov.requesthandlersystem.master.exception;

public class ConflictingRequestException extends RuntimeException {
    public ConflictingRequestException(String message) {
        super(message);
    }
}
