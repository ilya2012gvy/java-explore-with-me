package ru.practicum.exception;

public class DataException extends RuntimeException {
    public DataException(final String message) {
        super(message);
    }
}