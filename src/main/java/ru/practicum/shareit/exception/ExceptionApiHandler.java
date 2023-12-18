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
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(RuntimeException ex) {
        String message = ex.getMessage();
        log.info("Получен статус 404: {}, {}", ex.getMessage(), ex.getStackTrace());
        return new ErrorMessage(message, ex.getCause(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleConflictException(RuntimeException ex) {
        String message = ex.getMessage();
        log.info("Получен статус 409: {}, {}", ex.getMessage(), ex.getStackTrace());
        return new ErrorMessage(message, ex.getCause(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ErrorMessage handleUnsupportedOperationException(RuntimeException ex) {
        String message = ex.getMessage();
        log.info("Получен статус 501: {}, {}", ex.getMessage(), ex.getStackTrace());
        return new ErrorMessage(message, ex.getCause(), HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
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
