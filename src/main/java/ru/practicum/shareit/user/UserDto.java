package ru.practicum.shareit.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class UserDto {
    private long id;
    @NotBlank(message = "Логин не может быть пустым")
    private String name;
    @NotBlank(message = "Почта не может быть пустой")
    @Email(message = "Почта должна быть оформлена по правилам")
    private String email;
}
