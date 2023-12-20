package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public Item toItem(ItemDto item, long userId) {
        return Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .owner(userId)
                .available(item.getAvailable())
                .request(null) // пока не нужно
                .build();
    }
}
