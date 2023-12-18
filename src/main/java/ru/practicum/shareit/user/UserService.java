package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(UserDto userDto) {
        User createdUser = userStorage.addUser(UserMapper.toUser(userDto));
        log.info("Создан пользователь {}", createdUser);
        return UserMapper.toUserDto(createdUser);
    }

    public UserDto updateUserById(long userId, UserDto user) {
        User expectedUser = new User(checkUserId(userId));
        if (user.getName() != null) {
            expectedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            expectedUser.setEmail(user.getEmail());
        }
        User updatedUser = userStorage.updateUser(expectedUser);
        log.info("Обновлен пользователь с id {}", userId);
        return UserMapper.toUserDto(updatedUser);
    }

    public UserDto getUserById(long id) {
        User findedUser = checkUserId(id);
        log.info("Получен пользователь с id {}", id);
        return UserMapper.toUserDto(findedUser);
    }

    public List<UserDto> getAllUsers() {
        List<UserDto> users = userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto).collect(Collectors.toList());
        log.info("Получен список из {} пользователей", users.size());
        return users;
    }

    public void deleteUserById(long id) {
        checkUserId(id);
        userStorage.deleteUserById(id);
        log.info("Удален пользователь с id {}", id);
    }

    private User checkUserId(long userId) {
        return userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не существует", userId)));
    }
}
