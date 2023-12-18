package ru.practicum.shareit.item;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private ItemRequest request; // != null если не по запросу

    public Item(Item item) {
        this.id = item.id;
        this.name = item.name;
        this.description = item.description;
        this.available = item.available;
        this.owner = item.owner;
        this.request = item.request;
    }
}
