package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.CommentDtoResponse;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
public class ItemDto {

    private long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус не может отсутствовать")
    private Boolean available;

    private Long request;

    private BookingForItemDto lastBooking;

    private BookingForItemDto nextBooking;

    private List<CommentDtoResponse> comments;
}
