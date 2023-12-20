package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public ItemDto createItem(long userId, ItemDto item) {
        checkUserId(userId);
        Item createdItem = itemStorage.addItem(userId, ItemMapper.toItem(item, userId));
        log.info("Пользователь с id {} создал вещь {}", userId, createdItem);
        return ItemMapper.toItemDto(createdItem);
    }

    public ItemDto updateItem(long userId, long itemId, ItemDto item) {
        checkUserId(userId);
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
        log.info("Пользователь с id {} обновил вещь с id {}", userId, itemId);
        return ItemMapper.toItemDto(expectedItem);
    }

    public ItemDto getItem(long itemId) {
        Item foundItem = checkItemId(itemId);
        log.info("Получена вещь с id {}", itemId);
        return ItemMapper.toItemDto(foundItem);
    }

    public List<ItemDto> getItemsByUser(long userId) {
        checkUserId(userId);
        List<ItemDto> itemsByUser = itemStorage.getItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
        log.info("Получен список из {} вещей для пользователя с id {}", itemsByUser.size(), userId);
        return itemsByUser;
    }

    public List<ItemDto> searchItems(String query) {
        if (query.isBlank()) {
            log.info("Получен список из 0 вещей по запросу '{}'", query);
            return Collections.emptyList();
        }
        List<ItemDto> foundItems = itemStorage.searchItems(query).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
        log.info("Получен список из {} вещей по запросу '{}'", foundItems.size(), query);
        return foundItems;
    }

    private void checkUserId(long userId) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не существует", userId)));
    }

    private Item checkItemId(long itemId) {
        return itemStorage.getItemById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id %d не существует", itemId)));
    }
}
