package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.item.comment.CommentDtoRequest;
import ru.practicum.shareit.item.comment.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDtoShort createItem(@RequestHeader(Constant.OWNER_ID) long userId,
                                   @Validated(Marker.OnCreate.class) @RequestBody ItemDtoShort item) {
        log.info("Получен запрос от пользователя с id {} на создание вещи", userId);
        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoShort updateItem(@RequestHeader(Constant.OWNER_ID) long userId,
                                   @PathVariable long itemId,
                                   @Validated(Marker.OnUpdate.class) @RequestBody ItemDtoShort item) {
        log.info("Получен запрос от пользователя с id {} на обновление вещи", userId);
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(Constant.OWNER_ID) long userId,
                           @PathVariable long itemId) {
        log.info("Получен запрос на получение вещи с id {} от пользователя {}", itemId, userId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader(Constant.OWNER_ID) long userId,
                                        @RequestParam(name = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
                                        @RequestParam(name = "size", defaultValue = "5", required = false) @Positive Integer size) {
        log.info("Получен запрос от пользователя с id {} на получение списка его вещей", userId);
        return itemService.getItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDtoShort> searchItems(@RequestParam String text,
                                          @RequestParam(name = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
                                          @RequestParam(name = "size", defaultValue = "5", required = false) @Positive Integer size) {
        log.info("Получен запрос на поиск '{}' среди вещей", text);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@PathVariable long itemId,
                                            @RequestHeader(Constant.OWNER_ID) long userId,
                                            @Valid @RequestBody CommentDtoRequest comment) {
        log.info("Получен запрос на оставление комментария на вещь '{}'", itemId);
        return itemService.createComment(itemId, userId, comment);
    }
}
