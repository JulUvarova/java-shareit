package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.xml.bind.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionApiHandlerTest {
    private ExceptionApiHandler apiHandler;

    @BeforeEach
    void setUp() {
        apiHandler = new ExceptionApiHandler();
    }

    @Test
    void handleNotFoundException() {
        NotFoundException notFoundException = new NotFoundException("Smth is not found");

        ErrorMessage response = apiHandler.handleNotFoundException(notFoundException);

        assertEquals(notFoundException.getMessage(), response.getError());
    }

    @Test
    void handleConflictException() {
        ConflictException conflictException = new ConflictException("Smth is wrong");

        ErrorMessage response = apiHandler.handleConflictException(conflictException);

        assertEquals(conflictException.getMessage(), response.getError());
    }

    @Test
    void handleUnsupportedOperationException() {
        UnsupportedOperationException  unsupportedOperationException = new UnsupportedOperationException("Smth is not support");

        ErrorMessage response = apiHandler.handleUnsupportedOperationException(unsupportedOperationException);

        assertEquals(unsupportedOperationException.getMessage(), response.getError());
    }

//    @Test
//    void handleMethodArgumentNotValidException() {
//        MethodArgumentNotValidException argumentNotValidException = new MethodArgumentNotValidException(MethodParameter.forParameter(1));
//
//        ErrorMessage response = apiHandler.handleMethodArgumentNotValidException(argumentNotValidException);
//
//        assertEquals(argumentNotValidException.getMessage(), response.getError());
//    }

    @Test
    void handleBookingException() {
        BookingStatusException statusException = new BookingStatusException("Smth With Booking");

        ErrorMessage response = apiHandler.handleBookingException(statusException);

        assertEquals(statusException.getMessage(), response.getError());
    }

//    @Test
//    void handleUnhandledException() {
//    }
}