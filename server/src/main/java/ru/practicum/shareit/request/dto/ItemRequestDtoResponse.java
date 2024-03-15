package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
public class ItemRequestDtoResponse {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<ItemDtoForRequest> items;
}
