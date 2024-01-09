package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public ItemDtoShort toItemDtoShort(Item item) {
        return ItemDtoShort.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }

    public Item toItem(ItemDtoShort item, long userId) {
        return Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .owner(userId)
                .available(item.getAvailable())
                .request(null) // пока не нужно
                .build();
    }

    public ItemDto toItemDto(Item item, BookingForItemDto last, BookingForItemDto next, List<CommentDtoResponse> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .nextBooking(next)
                .lastBooking(last)
                .comments(comments)
                .build();
    }
}
