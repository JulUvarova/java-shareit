package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage,
                       BookingStorage bookingStorage, CommentStorage commentStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingStorage = bookingStorage;
        this.commentStorage = commentStorage;
    }

    @Transactional
    public ItemDtoShort createItem(long userId, ItemDtoShort item) {
        checkUserId(userId);
        Item createdItem = itemStorage.save(ItemMapper.toItem(item, userId));
        log.info("Пользователь с id {} создал вещь {}", userId, createdItem);
        return ItemMapper.toItemDtoShort(createdItem);
    }

    @Transactional
    public ItemDtoShort updateItem(long userId, long itemId, ItemDtoShort item) {
        Item expectedItem = checkItemId(itemId);
        if (expectedItem.getOwner() != userId) {
            throw new NotFoundException(
                    String.format("Пользователь с id %d не являеться владельщем вещи с id %d", userId, itemId));
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            expectedItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            expectedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            expectedItem.setAvailable(item.getAvailable());
        }
        itemStorage.save(expectedItem);
        log.info("Пользователь с id {} обновил вещь с id {}", userId, itemId);
        return ItemMapper.toItemDtoShort(expectedItem);
    }

    @Transactional(readOnly = true)
    public ItemDto getItem(long itemId, long userId) {
        Item item = checkItemId(itemId);
        List<CommentDtoResponse> comments = commentStorage.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDtoResponse).collect(Collectors.toList());
        if (item.getOwner() == userId) {
            LocalDateTime now = LocalDateTime.now();
            Booking nextBooking = bookingStorage.findFirst1ByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId,
                    now, Status.APPROVED).orElse(null);
            Booking lastBooking = bookingStorage.findFirst1ByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemId,
                    now, Status.APPROVED).orElse(null);

            log.info("Получена вещь с id {}", itemId);
            return ItemMapper.toItemDto(item, BookingMapper.toBookingForItemDto(lastBooking),
                    BookingMapper.toBookingForItemDto(nextBooking),
                    comments);
        }
        log.info("Получена вещь с id {}", itemId);
        return ItemMapper.toItemDto(item, null, null, comments);
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByUser(long userId) {
        Map<Long, Item> itemMap = itemStorage.findAllItemsByOwner(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<Booking>> nextBookings = bookingStorage.findAllByItemIdInAndStatusAndStartAfter(itemMap.keySet(), Status.APPROVED, now)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Booking>> lastBookings = bookingStorage.findAllByItemIdInAndStatusAndEndBefore(itemMap.keySet(), Status.APPROVED, now)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Map<Long, List<Comment>> comments = commentStorage.findAllByItemIdIn(itemMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(Comment::getItemId));

        List<ItemDto> itemsByUser = itemMap.values()
                .stream()
                .map(i -> addBookingsAndComments(i, lastBookings.get(i.getId()), nextBookings.get(i.getId()), comments.get(i.getId())))
                .collect(Collectors.toList());

        log.info("Получен список из {} вещей для пользователя с id {}", itemsByUser.size(), userId);
        return itemsByUser;
    }

    @Transactional(readOnly = true)
    public List<ItemDtoShort> searchItems(String query) {
        if (query.isBlank()) {
            log.info("Получен список из 0 вещей по запросу '{}'", query);
            return Collections.emptyList();
        }
        List<ItemDtoShort> foundItems = itemStorage.search(query).stream()
                .map(ItemMapper::toItemDtoShort).collect(Collectors.toList());
        log.info("Получен список из {} вещей по запросу '{}'", foundItems.size(), query);
        return foundItems;
    }

    @Transactional
    public CommentDtoResponse createComment(long itemId, long userId, CommentDtoRequest commentDto) {
        User user = checkUserId(userId);
        bookingStorage.findFirst1ByItemIdAndBookerIdAndEndBeforeOrderByEndDesc(itemId, userId, LocalDateTime.now())
                .orElseThrow(() -> new BookingStatusException("Нельзя оставить коммантарий"));
        Comment comment = commentStorage.save(CommentMapper.toComment(commentDto, user, itemId));
        log.info("Получен комментарий от пользователя {}", userId);
        return CommentMapper.toCommentDtoResponse(comment);
    }

    private User checkUserId(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не существует", userId)));
    }

    private Item checkItemId(long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id %d не существует", itemId)));
    }

    private ItemDto addBookingsAndComments(Item item, List<Booking> last, List<Booking> next, List<Comment> comments) {
        Booking findedLast = null;
        Booking findedNext = null;
        List<CommentDtoResponse> commentList = null;
        if (last != null) {
            findedLast = last.stream().max(Comparator.comparing(Booking::getEnd)).orElse(null);
        }
        if (next != null) {
            findedNext = next.stream().min(Comparator.comparing(Booking::getStart)).orElse(null);
        }
        if (comments != null) {
            commentList = comments.stream().map(CommentMapper::toCommentDtoResponse).collect(Collectors.toList());
        }

        return ItemMapper.toItemDto(item, BookingMapper.toBookingForItemDto(findedLast),
                BookingMapper.toBookingForItemDto(findedNext), commentList);
    }

}
