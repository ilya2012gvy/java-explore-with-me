package ru.practicum.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String massage) {
        super(massage);
    }
}