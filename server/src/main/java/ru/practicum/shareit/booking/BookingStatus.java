package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING, // новое, ожидает одобрения
    APPROVED, // подтверждено владельцем
    REJECTED, // отклонено владельцем
    CANCELED // отменено создателем
}
