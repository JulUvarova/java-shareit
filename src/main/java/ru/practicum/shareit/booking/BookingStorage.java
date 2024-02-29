package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByItemOwnerId(long owner, PageRequest pageRequest);

    Page<Booking> findAllByBookerId(long bookerId, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime start, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(long owner, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndEndBefore(long owner, LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStartAfter(long owner, LocalDateTime start, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStatus(long owner, BookingStatus status, PageRequest pageRequest);

    boolean existsByItemIdAndBookerIdAndEndBefore(long itemId, long userId, LocalDateTime now);
}
