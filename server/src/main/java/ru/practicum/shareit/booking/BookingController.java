package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.constant.Constant;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader(Constant.OWNER_ID) long userId,
                                            @RequestBody BookingDtoRequest bookingDto) {
        log.info("Получен запрос от пользователя с id {} на бронирование вещи с id {}", userId, bookingDto.getItemId());
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approvedBooking(@RequestHeader(Constant.OWNER_ID) long userId,
                                              @PathVariable long bookingId,
                                              @RequestParam Boolean approved) {
        log.info("Получен запрос от владельца вещи с id {} на {} бронирование", bookingId, approved);
        return bookingService.approvedBooking(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@RequestHeader(Constant.OWNER_ID) long userId,
                                             @PathVariable long bookingId) {
        log.info("Получен запрос от пользователя с id {} на просмотр бронирования {}", userId, bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getSortBookingByUser(@RequestHeader(Constant.OWNER_ID) long userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "5") Integer size) {
        log.info("Получен запрос от пользователя с id {} на просмотр списка бронирований с состоянием {}", userId, state);
        return bookingService.getSortBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getSortBookingByOwner(@RequestHeader(Constant.OWNER_ID) long userId,
                                                          @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @RequestParam(name = "size", defaultValue = "5") Integer size) {
        log.info("Получен запрос от пользователя с id {} на просмотр списка бронирований с состоянием {}", userId, state);
        return bookingService.getSortBookingByOwner(userId, state, from, size);
    }
}
