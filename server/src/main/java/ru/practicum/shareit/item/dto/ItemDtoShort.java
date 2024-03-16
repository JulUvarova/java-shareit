package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class ItemDtoShort {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
