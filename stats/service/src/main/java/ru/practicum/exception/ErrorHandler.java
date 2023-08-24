package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<ErrorResponse> handleThereIsNoSuchUserException(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(new ErrorResponse(Objects.requireNonNull(ex.getFieldError()).getDefaultMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataException(final DataException e) {
        return new ErrorResponse(e.getMessage());
    }
}