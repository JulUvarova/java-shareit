package ru.practicum.shareit.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorMessage {
    private final String message;
    private final Throwable throwable;
    private final HttpStatus httpStatus;
}
