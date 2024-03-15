package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.pagination.Paginator;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    private ItemService itemService;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private ItemRequestStorage requestStorage;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    private User testUser;
    private PageRequest page;
    private Item testItem;
    private ItemDtoShort itemDtoShort;

    @BeforeEach
    void init() {
        page = Paginator.simplePage(1, 5);

        itemDtoShort = new ItemDtoShort();

        testUser = User.builder()
                .id(1L)
                .name("UserName")
                .email("email@mail.ru")
                .build();

        testItem = Item.builder()
                .id(1L)
                .name("Brain")
                .description("Amazing brain")
                .owner(testUser)
                .available(true)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .build();
    }

    @Test
    void createItem_whenValidUserAndValidItemReq_thenReturnItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(itemStorage.save(ItemMapper.toItem(itemDtoShort, testUser)))
                .thenReturn(testItem);

        ItemDtoShort actualItem = itemService.createItem(testUser.getId(), new ItemDtoShort());
        assertEquals(ItemMapper.toItemDtoShort(testItem), actualItem);
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(itemStorage, times(1)).save(ItemMapper.toItem(itemDtoShort, testUser));
    }

    @Test
    void createItem_whenInvalidUserId_thenReturnException() {
        long userId = 1L;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.createItem(userId, itemDtoShort));

        assertEquals(String.format("Пользователь с id %d не существует", userId), exception.getMessage());
        verify(userStorage, times(1)).findById(userId);
        verify(requestStorage, times(0)).findById(itemDtoShort.getRequestId());
        verify(itemStorage, times(0)).save(ItemMapper.toItem(itemDtoShort, testUser));
    }

    @Test
    void createItem_whenInvalidItemReqId_thenReturnException() {
        ItemDtoShort itemDtoShort = new ItemDtoShort();
        itemDtoShort.setRequestId(5L);
        long userId = 1L;
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.createItem(userId, itemDtoShort));

        assertEquals(String.format("Запрос с id %d не существует", itemDtoShort.getRequestId()), exception.getMessage());
        verify(userStorage, times(1)).findById(userId);
        verify(requestStorage, times(1)).findById(itemDtoShort.getRequestId());
        verify(itemStorage, times(0)).save(ItemMapper.toItem(itemDtoShort, testUser));
    }

    @Test
    void updateItem_whenValidItem_thenReturnItem() {
        when(itemStorage.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        ItemDtoShort newItem = ItemDtoShort.builder()
                .id(1L)
                .name("New Brain")
                .description("Really new brain")
                .requestId(null)
                .available(true)
                .build();

        ItemDtoShort actualItemDto = itemService.updateItem(testUser.getId(), testItem.getId(), newItem);
        assertEquals(newItem, actualItemDto);

        verify(itemStorage).save(itemArgumentCaptor.capture());
        Item actualItem = itemArgumentCaptor.getValue();

        assertEquals("New Brain", actualItem.getName());
        assertEquals("Really new brain", actualItem.getDescription());
    }

    @Test
    void updateItem_whenInvalidItemId_thenReturnException() {
        when(itemStorage.findById(testItem.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.updateItem(testUser.getId(), testItem.getId(), itemDtoShort));

        assertEquals(String.format("Вещь с id %d не существует", testItem.getId()), exception.getMessage());
        verify(requestStorage, times(0)).findById(itemDtoShort.getRequestId());
        verify(itemStorage, times(0)).save(ItemMapper.toItem(itemDtoShort, testUser));
    }

    @Test
    void updateItem_whenEmptyNameDescriptionAvailable_thenReturnItemWithOldFields() {
        when(itemStorage.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        ItemDtoShort newItem = ItemDtoShort.builder()
                .id(1L)
                .name("")
                .description("")
                .requestId(null)
                .available(null)
                .build();

        ItemDtoShort actualItemDto = itemService.updateItem(testUser.getId(), testItem.getId(), newItem);
        assertNotEquals(newItem, actualItemDto);

        verify(itemStorage).save(itemArgumentCaptor.capture());
        Item actualItem = itemArgumentCaptor.getValue();

        assertEquals("Brain", actualItem.getName());
        assertEquals("Amazing brain", actualItem.getDescription());
    }

    @Test
    void getItem_whenValidItemIdAndOwnerId_thenReturnItemForOwner() {
        when(itemStorage.findById(testItem.getId()))
                .thenReturn(Optional.of(testItem));
        when(commentStorage.findAllByItemId(testItem.getId())).thenReturn(List.of());

        ItemDto actualItem = itemService.getItem(testItem.getId(), testUser.getId());

        assertEquals(ItemMapper.toItemDto(testItem, List.of()), actualItem);
        verify(itemStorage, times(1)).findById(testUser.getId());
        verify(commentStorage, times(1)).findAllByItemId(testItem.getId());
    }

    @Test
    void getItem_whenValidItemId_thenReturnItem() {
        when(itemStorage.findById(testItem.getId()))
                .thenReturn(Optional.of(testItem));
        when(commentStorage.findAllByItemId(testItem.getId())).thenReturn(List.of());

        ItemDto actualItem = itemService.getItem(testItem.getId(), 100L);

        assertEquals(ItemMapper.toItemDto(testItem, List.of()), actualItem);
        verify(itemStorage, times(1)).findById(testUser.getId());
        verify(commentStorage, times(1)).findAllByItemId(testItem.getId());
    }

    @Test
    void getItem_whenInvalidItemId_thenReturnException() {
        when(itemStorage.findById(testItem.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.getItem(testUser.getId(), testUser.getId()));

        assertEquals(String.format("Вещь с id %d не существует", testItem.getId()), exception.getMessage());
        verify(itemStorage, times(1)).findById(testItem.getId());
        verify(commentStorage, times(0)).findAllByItemId(testItem.getId());
    }

    @Test
    void getItemsByUser_whenValidUserId_thenReturnItemList() {
        List<Item> items = new ArrayList<>();
        items.add(testItem);

        when(itemStorage.findAllItemsByOwnerId(testUser.getId(), page))
                .thenReturn(items);
        when(commentStorage.findAllByItemIdIn(anySet()))
                .thenReturn(List.of());

        List<ItemDto> actualList = itemService.getItemsByUser(testUser.getId(), 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(ItemMapper.toItemDto(testItem, List.of()), actualList.get(0));
        verify(itemStorage, times(1)).findAllItemsByOwnerId(testUser.getId(), page);
    }

    @Test
    void searchItems_whenValidQuery_thenReturnItemList() {
        String query = "brain";
        when(itemStorage.search(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(testItem));

        List<ItemDtoShort> actualList = itemService.searchItems(query, 0, 5);

        assertEquals(1, actualList.size());
        assertEquals(ItemMapper.toItemDtoShort(testItem), actualList.get(0));
        verify(itemStorage, times(1)).search(query, page);
    }

    @Test
    void createComment_whenValidUserId_thenReturnComment() {
        LocalDateTime dateTime = LocalDateTime.now();
        Comment testComment = Comment.builder()
                .id(1L)
                .author(testUser)
                .itemId(testItem.getId())
                .text("Really good brain!")
                .created(dateTime)
                .build();

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(testComment);
        when(bookingStorage.existsByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);

        CommentDtoResponse actualComment = itemService.createComment(testItem.getId(), testUser.getId(), new CommentDtoRequest());

        assertEquals(CommentMapper.toCommentDtoResponse(testComment), actualComment);
        verify(userStorage, times(1)).findById(testUser.getId());
    }

    @Test
    void createComment_whenInvalidUserId_thenReturnException() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createComment(testItem.getId(), testUser.getId(), new CommentDtoRequest()));

        assertEquals(String.format("Пользователь с id %d не существует", testUser.getId()), exception.getMessage());
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(commentStorage, times(0)).save(new Comment());
        verify(bookingStorage, times(0)).existsByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class));
    }

    @Test
    void createComment_whenInvalidBookingEnd_thenReturnException() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(testUser));
        when(bookingStorage.existsByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(false);

        BookingStatusException exception = assertThrows(BookingStatusException.class,
                () -> itemService.createComment(testItem.getId(), testUser.getId(), new CommentDtoRequest()));

        assertEquals("Нельзя оставить комментарий", exception.getMessage());
        verify(userStorage, times(1)).findById(testUser.getId());
        verify(commentStorage, times(0)).save(new Comment());
        verify(bookingStorage, times(1)).existsByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class));
    }
}