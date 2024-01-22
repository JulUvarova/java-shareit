package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.CommentDtoResponse;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDtoResponse> comments;
}
