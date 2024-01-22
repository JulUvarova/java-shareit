package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwnerId(long owner, Sort sort);

    List<Booking> findAllByBookerId(long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(long owner, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(long owner, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(long owner, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(long owner, BookingStatus status, Sort sort);

    boolean existsByItemIdAndBookerIdAndEndBefore(long itemId, long userId, LocalDateTime now);
}
