package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.exception.BookingStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(Constant.USER_ID) long userId,
                                           @RequestBody @Valid BookingDtoRequest requestDto) {
        log.info("Запрос на бронирование {} пользователем {}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@RequestHeader(Constant.USER_ID) long userId,
                                                  @PathVariable long bookingId,
                                                  @RequestParam Boolean approved) {
        log.info("Запрос владельцем {} на изменение статуса {} для бронирования {}", userId, approved, bookingId);
        return bookingClient.approveBooking(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(Constant.USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Запрос от пользователя {} на просмотр бронирования {}", userId, bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(Constant.USER_ID) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "5") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingStatusException("Unknown state: " + stateParam));
        log.info("Запрос на бронирования со статусом {} от пользователя {}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(Constant.USER_ID) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "5") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingStatusException("Unknown state: " + stateParam));
        log.info("Запрос на бронирования со статусом {} от пользователя {}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }
}
