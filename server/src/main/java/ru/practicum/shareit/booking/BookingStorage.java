package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwnerId(long owner, PageRequest pageRequest);

    List<Booking> findAllByBookerId(long bookerId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime start, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(long owner, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndBefore(long owner, LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartAfter(long owner, LocalDateTime start, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatus(long owner, BookingStatus status, PageRequest pageRequest);

    boolean existsByItemIdAndBookerIdAndEndBefore(long itemId, long userId, LocalDateTime now);
}
