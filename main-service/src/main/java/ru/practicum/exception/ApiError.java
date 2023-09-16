package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class ApiError {
    String message;

    String reason;

    String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;

    List<StackTraceElement> errors;

    public ApiError(String message, Exception exception, HttpStatus status) {
        this.message = message;
        this.reason = exception.getMessage();
        this.status = status.name();
        this.timestamp = LocalDateTime.now();
        this.errors = List.of(exception.getStackTrace());
    }
}