package ru.practicum.shareit.booking;

public enum Status {
    WAITING, // новое, ожидает одобрения
    APPROVED, // подтверждено владельцем
    REJECTED, // отклонено владельцем
    CANCELED // отменено создателем
}
