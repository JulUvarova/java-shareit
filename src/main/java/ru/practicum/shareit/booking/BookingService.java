package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.pagination.Paginator;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingService {
    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public BookingService(BookingStorage bookingStorage, ItemStorage itemStorage, UserStorage userStorage) {
        this.bookingStorage = bookingStorage;
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Transactional
    public BookingDtoResponse createBooking(long userId, BookingDtoRequest bookingDto) {
        User booker = checkUserId(userId);
        Item item = checkItemId(bookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new BookingStatusException(String.format("Вещь с id %d не доступна для бронирования", item.getId()));
        }

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец не может бронировать свои вещи");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker, BookingStatus.WAITING);
        bookingStorage.save(booking);
        log.info("Пользователь с id {} забронировал вещь с id {}", userId, bookingDto.getItemId());
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Transactional
    public BookingDtoResponse approvedBooking(long userId, boolean approved, long bookingId) {
        Booking booking = checkBookingId(bookingId);

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(String.format(
                    "Пользователь с id %d не является владельцем вещи %d", userId, booking.getItem().getId()));
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingStatusException(String.format("Бронирование уже %s", booking.getStatus()));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking savedBooking = bookingStorage.save(booking);
        log.info("Владелец изменил статус бронирования с id {} на {}", bookingId, approved);
        return BookingMapper.toBookingDtoResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingById(long userId, long bookingId) {
        Booking booking = checkBookingId(bookingId);
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new NotFoundException(String.format(
                    "Пользователь с id %d не относится к этому бронированию", userId));
        }
        log.info("Получены данные бронирования с id {}", bookingId);
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getSortBookingByUser(long userId, String stateStr, Integer from, Integer size) {
        BookingStateStatus state = BookingStateStatus.toState(stateStr);
        checkUserId(userId);
        Page<Booking> bookings = null;
        PageRequest pagination = Paginator.withSort(from, size, Constant.SORT_BY_START_DESC);
        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByBookerId(userId, pagination);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pagination);
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), pagination);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now(), pagination);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pagination);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pagination);
                break;
        }
        log.info("Получен список бронирований");
        return bookings.stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getSortBookingByOwner(long userId, String stateStr, Integer from, Integer size) {
        BookingStateStatus state = BookingStateStatus.toState(stateStr);
        checkUserId(userId);
        Page<Booking> bookings = null;
        PageRequest pagination = Paginator.withSort(from, size, Constant.SORT_BY_START_DESC);
        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByItemOwnerId(userId, pagination);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pagination);
                break;
            case PAST:
                bookings = bookingStorage.findAllByItemOwnerIdAndEndBefore(userId, LocalDateTime.now(), pagination);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByItemOwnerIdAndStartAfter(userId, LocalDateTime.now(), pagination);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pagination);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pagination);
                break;
        }
        log.info("Получен список бронирований");
        return bookings.stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
    }

    private User checkUserId(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не существует", userId)));
    }

    private Item checkItemId(long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id %d не существует", itemId)));
    }

    private Booking checkBookingId(long bookingId) {
        return bookingStorage.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирование с id %d не существует", bookingId)));
    }

}
