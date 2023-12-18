package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
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
    @ResponseStatus(HttpStatus.OK)
    public ItemDto createItem(@RequestHeader(OWNER_ID) long userId, @Valid @RequestBody ItemDto item) {
        log.info("Получен запрос от пользователя с id {} на создание вещи", userId);
        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader(OWNER_ID) long userId,
                              @PathVariable long itemId, @RequestBody ItemDto item) {
        log.info("Получен запрос от пользователя с id {} на обновление вещи", userId);
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable long itemId) {
        log.info("Получен запрос на получение вещи с id {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItemsByUser(@RequestHeader(OWNER_ID) long userId) {
        log.info("Получен запрос от пользователя с id {} на получение списка его вещей", userId);
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен запрос на поиск '{}' среди вещей", text);
        return itemService.searchItems(text);
    }
}
