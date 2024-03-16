package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.pagination.Paginator;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestService itemRequestService;
    @Mock
    private ItemRequestStorage itemRequestStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    private ItemRequest testItemRequest;
    private User testUser;
    private PageRequest page;


    @BeforeEach
    void init() {
        page = Paginator.withSort(0, 5, Constant.SORT_BY_CREATED_DESC);

        testUser = User.builder()
                .id(1L)
                .name("UserName")
                .email("email@mail.ru")
                .build();

        testItemRequest = ItemRequest.builder()
                .id(1L)
                .description("need brains")
                .created(LocalDateTime.now())
                .requestor(testUser)
                .build();
    }

    @Test
    void createRequest_whenUserIsExist_thenReturnItemRequest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(itemRequestStorage.save(any(ItemRequest.class)))
                .thenReturn(testItemRequest);

        ItemRequestDtoResponse actualItemReq = itemRequestService.createRequest(testUser.getId(), new ItemRequestDtoRequest());

        assertEquals(ItemRequestMapper.toItemRequestDtoResponse(testItemRequest, List.of()), actualItemReq);
        verify(userStorage, times(1)).findById(testUser.getId());
    }

    @Test
    void createRequest_whenInvalidUserId_thenReturnItemRequest() {
        long userId = 1L;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(userId, new ItemRequestDtoRequest()));

        assertEquals(String.format("Пользователь с id %d не существует", userId), exception.getMessage());
        verify(userStorage, times(1)).findById(userId);
        verify(itemRequestStorage, times(0)).save(testItemRequest);
    }

    @Test
    void getRequestsByOwner_whenValidUser_thenReturnItemReqList() {
        List<ItemRequest> itemReqs = new ArrayList<>();
        itemReqs.add(testItemRequest);

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(itemRequestStorage.findAllByRequestorId(anyLong(), any(PageRequest.class)))
                .thenReturn(itemReqs);

        List<ItemRequestDtoResponse> actualList = itemRequestService.getRequestsByOwner(testUser.getId(), 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(ItemRequestMapper.toItemRequestDtoResponse(testItemRequest, List.of()), actualList.get(0));
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(itemRequestStorage, times(1)).findAllByRequestorId(testUser.getId(), page);
    }

    @Test
    void getRequestsByOwner_whenInvalidUserId_thenReturnException() {
        long userId = 1L;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.getRequestsByOwner(
                userId, 1, 1));

        assertEquals(String.format("Пользователь с id %d не существует", userId), exception.getMessage());
        verify(userStorage, times(1)).findById(userId);
        verify(itemRequestStorage, times(0)).findAllByRequestorId(userId, page);
    }

    @Test
    void getAllRequests_whenValidUserId_thenReturnItemReqList() {
        List<ItemRequest> itemReqs = new ArrayList<>();
        itemReqs.add(testItemRequest);

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(itemRequestStorage.findAllByRequestorIdNot(anyLong(), any(PageRequest.class)))
                .thenReturn(itemReqs);
        when(itemStorage.findAllByRequestIdIn(anySet()))
                .thenReturn(List.of());

        List<ItemRequestDtoResponse> actualList = itemRequestService.getAllRequests(testUser.getId(), 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(ItemRequestMapper.toItemRequestDtoResponse(testItemRequest, List.of()), actualList.get(0));
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(itemRequestStorage, times(1)).findAllByRequestorIdNot(testUser.getId(), page);
    }

    @Test
    void getAllRequests_whenInvalidUserId_thenReturnException() {
        long userId = 1L;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(
                userId, 1, 1));

        assertEquals(String.format("Пользователь с id %d не существует", userId), exception.getMessage());
        verify(userStorage, times(1)).findById(userId);
        verify(itemRequestStorage, times(0)).findAllByRequestorIdNot(userId, page);
    }

    @Test
    void getRequestsById_whenValidUserId_thenReturnItemReq() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(itemRequestStorage.findById(anyLong()))
                .thenReturn(Optional.of(testItemRequest));

        ItemRequestDtoResponse actualItemReq = itemRequestService.getRequestsById(testUser.getId(), testItemRequest.getId());

        assertEquals(ItemRequestMapper.toItemRequestDtoResponse(testItemRequest, List.of()), actualItemReq);
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(itemRequestStorage, times(1)).findById(testItemRequest.getId());
    }

    @Test
    void getRequestsById_whenInvalidUserId_thenReturnException() {
        long userId = 1L;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.getRequestsById(testUser.getId(), testItemRequest.getId()));

        assertEquals(String.format("Пользователь с id %d не существует", userId), exception.getMessage());
        verify(userStorage, times(1)).findById(userId);
        verify(itemRequestStorage, times(0)).findById(testItemRequest.getId());

    }

    @Test
    void getRequestsById_whenInvalidItemReqId_thenReturnException() {
        long userId = 1L;
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(itemRequestStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemRequestService.getRequestsById(testUser.getId(), testItemRequest.getId()));

        assertEquals(String.format("Запрос с id %d не существует", testItemRequest.getId()), exception.getMessage());
        verify(userStorage, times(1)).findById(userId);
        verify(itemRequestStorage, times(1)).findById(testItemRequest.getId());
    }
}