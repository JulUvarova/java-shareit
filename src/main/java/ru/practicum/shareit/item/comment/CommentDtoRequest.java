package ru.practicum.shareit.item.comment;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@SuperBuilder
public class CommentDtoRequest {
    private Long id;

    @NotBlank(message = "Отзыв не может быть пустым")
    private String text;

    private Long itemId;

    private Long authorId;
}
