package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.comment.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public ItemDtoShort toItemDtoShort(Item item) {
        return ItemDtoShort.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public Item toItem(ItemDtoShort item, User user) {
        return Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .owner(user)
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public ItemDto toItemDto(Item item, List<CommentDtoResponse> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .lastBooking(BookingMapper.toBookingForItemDto(item.getLastBooking()))
                .nextBooking(BookingMapper.toBookingForItemDto(item.getNextBooking()))
                .comments(comments)
                .build();
    }

    public ItemDtoForBooking toItemBooking(Item item) {
        return ItemDtoForBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}
