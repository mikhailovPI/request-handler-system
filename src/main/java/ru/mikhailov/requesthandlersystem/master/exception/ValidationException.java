package ru.mikhailov.requesthandlersystem.master.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
