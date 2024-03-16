package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@SuperBuilder
public class CommentDtoRequest {
    @Size(max = 512)
    @NotBlank(message = "Отзыв не может быть пустым")
    private String text;
}
