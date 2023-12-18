package ru.practicum.shareit.item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto item, long userId) {
        return Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .owner(userId)
                .available(item.getAvailable())
                .request(null) // пока не нужно
                .build();
    }
}
