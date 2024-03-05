package ru.mikhailov.requesthandlersystem.master.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
