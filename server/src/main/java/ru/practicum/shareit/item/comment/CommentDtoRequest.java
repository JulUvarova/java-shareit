package ru.practicum.shareit.item.comment;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class CommentDtoRequest {
    private String text;
}
