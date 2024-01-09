package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDtoRequest;
import ru.practicum.shareit.item.comment.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDtoShort createItem(@RequestHeader(OWNER_ID) long userId,
                                   @Valid @RequestBody ItemDtoShort item) {
        log.info("Получен запрос от пользователя с id {} на создание вещи", userId);
        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoShort updateItem(@RequestHeader(OWNER_ID) long userId,
                                   @PathVariable long itemId,
                                   @RequestBody ItemDtoShort item) {
        log.info("Получен запрос от пользователя с id {} на обновление вещи", userId);
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(OWNER_ID) long userId,
                           @PathVariable long itemId) {
        log.info("Получен запрос на получение вещи с id {} от пользователя {}", itemId, userId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader(OWNER_ID) long userId) {
        log.info("Получен запрос от пользователя с id {} на получение списка его вещей", userId);
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDtoShort> searchItems(@RequestParam String text) {
        log.info("Получен запрос на поиск '{}' среди вещей", text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@PathVariable long itemId,
                                            @RequestHeader(OWNER_ID) long userId,
                                            @Valid @RequestBody CommentDtoRequest comment) {
        log.info("Получен запрос на оставление комментария на вещь '{}'", itemId);
        return itemService.createComment(itemId, userId, comment);
    }
}
