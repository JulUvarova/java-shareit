package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.BookingStatusException;

public enum BookingStateStatus {
    ALL,
    CURRENT, // текущие
    PAST, // завершённые
    FUTURE, // будущие
    WAITING, // ожидающие подтверждения
    REJECTED; // отклонённые

    public static BookingStateStatus toState(String state) {
        switch (state) {
            case "ALL":
                return BookingStateStatus.ALL;
            case "CURRENT":
                return BookingStateStatus.CURRENT;
            case "PAST":
                return BookingStateStatus.PAST;
            case "FUTURE":
                return BookingStateStatus.FUTURE;
            case "WAITING":
                return BookingStateStatus.WAITING;
            case "REJECTED":
                return BookingStateStatus.REJECTED;
            default:
                throw new BookingStatusException(String.format("Unknown state: %s", state));
        }
    }
}
