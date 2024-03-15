package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(Constant.USER_ID) long userId,
                                             @Validated(Marker.OnCreate.class) @RequestBody ItemDtoShort item) {
        log.info("Получен запрос от пользователя с id {} на создание вещи", userId);
        return itemClient.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(Constant.USER_ID) long userId,
                                   @PathVariable long itemId,
                                   @Validated(Marker.OnUpdate.class) @RequestBody ItemDtoShort item) {
        log.info("Получен запрос от пользователя с id {} на обновление вещи", userId);
        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(Constant.USER_ID) long userId,
                           @PathVariable long itemId) {
        log.info("Получен запрос на получение вещи с id {} от пользователя {}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader(Constant.USER_ID) long userId,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(name = "size", defaultValue = "5") @Positive Integer size) {
        log.info("Получен запрос от пользователя с id {} на получение списка его вещей", userId);
        return itemClient.getItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                          @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(name = "size", defaultValue = "5") @Positive Integer size) {
        log.info("Получен запрос на поиск {} среди вещей", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable long itemId,
                                            @RequestHeader(Constant.USER_ID) long userId,
                                            @Valid @RequestBody CommentDtoRequest comment) {
        log.info("Получен запрос на оставление комментария на вещь {}", itemId);
        return itemClient.createComment(itemId, userId, comment);
    }
}
