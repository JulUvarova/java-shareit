package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@SuperBuilder
public class ItemDtoShort {

    private long id;

    @Size(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, max = 255)
    @NotBlank(groups = Marker.OnCreate.class, message = "Имя не может быть пустым")
    private String name;

    @Size(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, max = 512)
    @NotBlank(groups = Marker.OnCreate.class, message = "Описание не может быть пустым")
    private String description;

    @NotNull(groups = Marker.OnCreate.class, message = "Статус не может отсутствовать")
    private Boolean available;

    private Long request;
}
