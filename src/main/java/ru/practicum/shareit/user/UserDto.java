package ru.practicum.shareit.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@SuperBuilder
public class UserDto {

    private long id;

    @Size(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, max = 64)
    @NotBlank(groups = Marker.OnCreate.class, message = "Логин не может быть пустым")
    private String name;

    @Size(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, max = 320)
    @NotBlank(groups = Marker.OnCreate.class, message = "Почта не может быть пустой")
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Почта должна быть оформлена по правилам")
    private String email;
}
