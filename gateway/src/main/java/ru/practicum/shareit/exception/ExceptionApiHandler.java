package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.info("Получен статус 400: {}, {}", ex.getMessage(), ex.getStackTrace());
        return new ErrorMessage(message, ex.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleBookingException(BookingStatusException ex) {
        String message = ex.getMessage();
        log.info("Получен статус 400: {}, {}", ex.getMessage(), ex.getStackTrace());
        return new ErrorMessage(message, ex.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleUnhandledException(Throwable ex) {
        String message = ex.getMessage();
        log.info("Получен статус 500: {}, {}", ex.getMessage(), ex.getStackTrace());
        return new ErrorMessage(message, ex.getCause(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
