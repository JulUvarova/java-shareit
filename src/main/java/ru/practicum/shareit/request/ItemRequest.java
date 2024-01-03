package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class ItemRequest {
    private long id;
    private String description; // текст запроса с описанием item
    private User requestor;
    private LocalDateTime created; // когда создан запрос
}
