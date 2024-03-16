package ru.practicum.shareit.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorMessage {
    private final String error;
    private final Throwable throwable;
    private final HttpStatus httpStatus;
}
