package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwnerOrderByStartDesc(long owner);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(long owner, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(long owner, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndStartAfterOrderByStartDesc(long owner, LocalDateTime start);

    List<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(long owner, Status status);

    Optional<Booking> findFirst1ByItemIdAndBookerIdAndEndBeforeOrderByEndDesc(long itemId, long bookerId, LocalDateTime end);

    Optional<Booking> findFirst1ByItemIdAndStartAfterAndStatusOrderByStartAsc(long itemId, LocalDateTime start, Status status);

    Optional<Booking> findFirst1ByItemIdAndStartBeforeAndStatusOrderByStartDesc(long itemId, LocalDateTime end, Status status);

    List<Booking> findAllByItemIdInAndStatusAndEndBefore(Set<Long> itemId, Status status, LocalDateTime end);

    List<Booking> findAllByItemIdInAndStatusAndStartAfter(Set<Long> itemId, Status status, LocalDateTime start);
}
