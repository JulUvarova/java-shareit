package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@SuperBuilder
public class User {
    private long id;
    private String name;
    private String email;

    public User(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
