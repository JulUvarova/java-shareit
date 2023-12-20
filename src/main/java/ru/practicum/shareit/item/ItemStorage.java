package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ItemStorage {
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();
    private long count;

    public Item addItem(long userId, Item item) {
        item.setId(++count);
        final List<Item> items = userItemIndex.computeIfAbsent(userId, k -> new ArrayList<>());
        items.add(item);
        return item;
    }

    public Optional<Item> getItemById(long id) {
        for(Map.Entry<Long, List<Item>> entry : userItemIndex.entrySet()){
            for(Item item : entry.getValue()){
                if (item.getId() == id) {
                    return Optional.of(item);
                }
            }
        }
        return Optional.empty();
    }

    public List<Item> getItemsByUserId(long userId) {
        List<Item> itemsList = new ArrayList<>(userItemIndex.get(userId));
        return itemsList;
    }

    public List<Item> searchItems(String query) {
        String findText = query.toLowerCase();
        List<Item> foundItems = new ArrayList<>();
        for(Map.Entry<Long, List<Item>> entry : userItemIndex.entrySet()){
            for(Item item : entry.getValue()){
                if (item.getName().toLowerCase().contains(findText)
                        || item.getDescription().toLowerCase().contains(findText)
                        && item.getAvailable().equals(true)) {
                    foundItems.add(item);
                }
            }
        }
        return foundItems;
    }
}
