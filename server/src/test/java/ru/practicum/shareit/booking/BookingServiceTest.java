package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @InjectMocks
    private BookingService bookingService;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;
    private User testUser;
    private User testOwner;
    private PageRequest page;
    private Item testItem;
    private Booking testBooking;
    private BookingDtoRequest bookingRequest;

    @BeforeEach
    void init() {
        page = Paginator.simplePage(1, 5);

        testUser = User.builder()
                .id(1L)
                .name("UserName")
                .email("email@mail.ru")
                .build();

        testOwner = User.builder()
                .id(10L)
                .name("OwnerName")
                .email("owner@mail.ru")
                .build();

        testItem = Item.builder()
                .id(1L)
                .name("Brain")
                .description("Amazing brain")
                .owner(testOwner)
                .available(true)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .build();

        bookingRequest = BookingDtoRequest.builder()
                .itemId(testItem.getId())
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusDays(1L))
                .build();

        testBooking = BookingMapper.toBooking(bookingRequest, testItem, testUser, BookingStatus.WAITING);
        testBooking.setId(1L);

        page = Paginator.withSort(0, 5, Constant.SORT_BY_START_DESC);
    }

    @Test
    void createBooking_whenValidUserIdAndItemIsAvailable_thenReturnBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(testItem));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(testBooking);

        BookingDtoResponse actualBooking = bookingService.createBooking(testUser.getId(), bookingRequest);
        actualBooking.setId(1L);

        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualBooking);
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(itemStorage, times(1)).findById(testItem.getId());
    }

    @Test
    void createBooking_whenInvalidUserId_thenReturnException() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(testUser.getId(), bookingRequest));

        assertEquals(String.format("Пользователь с id %d не существует", testUser.getId()), exception.getMessage());
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(itemStorage, times(0)).findById(testItem.getId());
        verify(bookingStorage, times(0)).save(testBooking);
    }

    @Test
    void createBooking_whenInvalidUserIsOwner_thenReturnException() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testOwner));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(testItem));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(testOwner.getId(), bookingRequest));

        assertEquals("Владелец не может бронировать свои вещи", exception.getMessage());
        verify(userStorage, times(1)).findById(testOwner.getId());
        verify(itemStorage, times(1)).findById(testItem.getId());
        verify(bookingStorage, times(0)).save(testBooking);
    }

    @Test
    void createBooking_whenInvalidItemId_thenReturnException() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(testUser.getId(), bookingRequest));

        assertEquals(String.format("Вещь с id %d не существует", testItem.getId()), exception.getMessage());
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(itemStorage, times(1)).findById(testItem.getId());
        verify(bookingStorage, times(0)).save(testBooking);
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenReturnException() {
        testItem.setAvailable(false);

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(testItem));

        BookingStatusException exception = assertThrows(BookingStatusException.class,
                () -> bookingService.createBooking(testUser.getId(), bookingRequest));

        assertEquals(String.format("Вещь с id %d не доступна для бронирования", testItem.getId()), exception.getMessage());
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(itemStorage, times(1)).findById(testItem.getId());
        verify(bookingStorage, times(0)).save(testBooking);
    }

    @Test
    void approvedBooking_whenOwnerAndBookingIsNotWaiting_thenReturnBooking() {
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(testBooking);

        BookingDtoResponse actualBookingDto = bookingService.approvedBooking(testOwner.getId(), true, testBooking.getId());

        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualBookingDto);

        verify(bookingStorage).save(bookingArgumentCaptor.capture());
        Booking actualBooking = bookingArgumentCaptor.getValue();

        assertEquals(BookingStatus.APPROVED, actualBooking.getStatus());
        verify(bookingStorage, times(1)).findById(testBooking.getId());
        verify(bookingStorage, times(1)).save(testBooking);
    }

    @Test
    void approvedBooking_whenInvalidBookingId_thenReturnException() {
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approvedBooking(testOwner.getId(), true, testBooking.getId()));

        assertEquals(String.format("Бронирование с id %d не существует", testBooking.getId()), exception.getMessage());
        verify(bookingStorage, times(1)).findById(testBooking.getId());
        verify(bookingStorage, times(0)).save(testBooking);
    }

    @Test
    void approvedBooking_whenBookingStatusIsApproved_thenReturnException() {
        testBooking.setStatus(BookingStatus.APPROVED);
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));

        BookingStatusException exception = assertThrows(BookingStatusException.class,
                () -> bookingService.approvedBooking(testOwner.getId(), true, testBooking.getId()));

        assertEquals(String.format("Бронирование уже %s", testBooking.getStatus()), exception.getMessage());
        verify(bookingStorage, times(1)).findById(testBooking.getId());
        verify(bookingStorage, times(0)).save(testBooking);
    }

    @Test
    void approvedBooking_whenBookingStatusIsReject_thenReturnException() {
        testBooking.setStatus(BookingStatus.REJECTED);
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));

        BookingStatusException exception = assertThrows(BookingStatusException.class,
                () -> bookingService.approvedBooking(testOwner.getId(), true, testBooking.getId()));

        assertEquals(String.format("Бронирование уже %s", testBooking.getStatus()), exception.getMessage());
        verify(bookingStorage, times(1)).findById(testBooking.getId());
        verify(bookingStorage, times(0)).save(testBooking);
    }

    @Test
    void approvedBooking_whenUserIsNotOwner_thenReturnException() {
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approvedBooking(testUser.getId(), true, testBooking.getId()));

        assertEquals(String.format("Пользователь с id %d не является владельцем вещи %d", testUser.getId(), testBooking.getId()), exception.getMessage());
        verify(bookingStorage, times(1)).findById(testBooking.getId());
        verify(bookingStorage, times(0)).save(testBooking);
    }

    @Test
    void getBookingById_whenOwnerOrBookerAndBookingExist_thenReturnBooking() {
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));

        BookingDtoResponse actualBooking = bookingService.getBookingById(testOwner.getId(), testBooking.getId());

        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualBooking);
        verify(bookingStorage, times(1)).findById(testBooking.getId());
    }

    @Test
    void getBookingById_whenInvalidUserId_thenReturnException() {
        long userId = 100L;
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(testBooking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(userId, testBooking.getId()));

        assertEquals(String.format("Пользователь с id %d не относится к этому бронированию", userId), exception.getMessage());
        verify(bookingStorage, times(1)).findById(testBooking.getId());
    }

    @Test
    void getBookingById_whenInvalidBookingId_thenReturnException() {
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(testOwner.getId(), testBooking.getId()));

        assertEquals(String.format("Бронирование с id %d не существует", testBooking.getId()), exception.getMessage());
        verify(bookingStorage, times(1)).findById(testBooking.getId());
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusAll_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByBookerId(anyLong(), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByUser(testUser.getId(), "ALL", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(bookingStorage, times(1)).findAllByBookerId(testUser.getId(), page);
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusCurrent_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByUser(testUser.getId(), "CURRENT", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testUser.getId());
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusPast_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByUser(testUser.getId(), "PAST", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testUser.getId());
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusFuture_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByUser(testUser.getId(), "FUTURE", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testUser.getId());
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusWaiting_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByUser(testUser.getId(), "WAITING", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testUser.getId());
    }

    @Test
    void getSortBookingByUser_whenValidUserIdAndStatusRejected_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByUser(testUser.getId(), "REJECTED", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testUser.getId());
    }

    @Test
    void getSortBookingByUser_whenInvalidStatus_thenReturnException() {
        String state = "INVALID_STATUS";

        BookingStatusException exception = assertThrows(BookingStatusException.class,
                () -> bookingService.getSortBookingByUser(testUser.getId(), state, 0, 5));

        assertEquals(String.format("Unknown state: %s", state), exception.getMessage());
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void getSortBookingByUser_whenInvalidUserId_thenReturnException() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getSortBookingByUser(testUser.getId(), "REJECTED", 0, 5));

        assertEquals(String.format("Пользователь с id %d не существует", testUser.getId()), exception.getMessage());
        verify(userStorage, times(1)).findById(testUser.getId());
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndBookingExist_thenReturnBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testOwner));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByItemOwnerId(anyLong(), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByOwner(testOwner.getId(), "ALL", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testOwner.getId());
        verify(bookingStorage, times(1)).findAllByItemOwnerId(testOwner.getId(), page);
    }

    @Test
    void getSortBookingByOwner_whenInvalidUserId_thenReturnException() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getSortBookingByOwner(testOwner.getId(), "REJECTED", 0, 5));

        assertEquals(String.format("Пользователь с id %d не существует", testOwner.getId()), exception.getMessage());
        verify(userStorage, times(1)).findById(testOwner.getId());
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusAll_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testOwner));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByItemOwnerId(anyLong(), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByOwner(testOwner.getId(), "ALL", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testOwner.getId());
        verify(bookingStorage, times(1)).findAllByItemOwnerId(testOwner.getId(), page);
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusCurrent_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testOwner));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByOwner(testOwner.getId(), "CURRENT", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testOwner.getId());
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusPast_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testOwner));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByItemOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByOwner(testOwner.getId(), "PAST", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testOwner.getId());
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusFuture_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testOwner));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByItemOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByOwner(testOwner.getId(), "FUTURE", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testOwner.getId());
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusWaiting_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testOwner));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByItemOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByOwner(testOwner.getId(), "WAITING", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testOwner.getId());
    }

    @Test
    void getSortBookingByOwner_whenValidUserIdAndStatusRejected_thenReturnBookingList() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testOwner));
        List<Booking> bookings = List.of(testBooking);
        when(bookingStorage.findAllByItemOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(bookings);

        List<BookingDtoResponse> actualList = bookingService.getSortBookingByOwner(testOwner.getId(), "REJECTED", 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(BookingMapper.toBookingDtoResponse(testBooking), actualList.get(0));
        verify(userStorage, times(1)).findById(testOwner.getId());
    }

    @Test
    void getSortBookingByOwner_whenInvalidStatus_thenReturnException() {
        String state = "INVALID_STATUS";

        BookingStatusException exception = assertThrows(BookingStatusException.class,
                () -> bookingService.getSortBookingByOwner(testOwner.getId(), state, 0, 5));

        assertEquals(String.format("Unknown state: %s", state), exception.getMessage());
        verifyNoInteractions(bookingStorage);
    }
}