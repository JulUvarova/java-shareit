package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorage {
    private Map<Long, Item> items = new HashMap<>();
    private long count;

    public Item addItem(Item item) {
        item.setId(++count);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> getItemById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> getItemsByUserId(long userId) {
        List<Item> itemsList = new ArrayList<>(items.values());
        List<Item> itemsByUserId = itemsList.stream().filter(i -> i.getOwner() == userId).collect(Collectors.toList());
        return itemsByUserId;
    }

    public List<Item> searchItems(String query) {
        List<Item> itemsList = new ArrayList<>(items.values());
        List<Item> foundItems = itemsList.stream()
                .filter(i -> (i.getName().toLowerCase().contains(query.toLowerCase())
                        || i.getDescription().toLowerCase().contains(query.toLowerCase()))
                        && i.getAvailable().equals(true)
                ).collect(Collectors.toList());
        return foundItems;
    }

    public void deleteItemById(long id) {
        items.remove(id);
    }
}
