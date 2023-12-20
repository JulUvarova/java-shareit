package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;

import java.util.*;

@Repository
public class UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private long count;

    public User addUser(User user) {
        emailCheck(user.getId(), user.getEmail());
        user.setId(++count);
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void deleteUserById(long id) {
        users.remove(id);
    }

    public void emailCheck(long id, String email) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            if (entry.getValue().getEmail().equals(email) && entry.getKey() != id) {
                throw new ConflictException(String.format("Пользователь с email %s уже существует", email));
            }
        }
    }
}
