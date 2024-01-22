package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

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

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User createdUser = userStorage.save(UserMapper.toUser(userDto));
        log.info("Создан пользователь {}", createdUser);
        return UserMapper.toUserDto(createdUser);
    }

    @Transactional
    public UserDto updateUserById(long userId, UserDto user) {
        User expectedUser = checkUserId(userId);

        if (user.getName() != null  && !user.getName().isBlank()) {
            expectedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            expectedUser.setEmail(user.getEmail());
        }
        expectedUser.setId(userId);
        userStorage.save(expectedUser);

        log.info("Обновлен пользователь с id {}", userId);
        return UserMapper.toUserDto(expectedUser);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(long id) {
        User findedUser = checkUserId(id);
        log.info("Получен пользователь с id {}", id);
        return UserMapper.toUserDto(findedUser);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userStorage.findAll().stream()
                .map(UserMapper::toUserDto).collect(Collectors.toList());
        log.info("Получен список из {} пользователей", users.size());
        return users;
    }

    @Transactional
    public void deleteUserById(long id) {
        checkUserId(id);
        userStorage.deleteById(id);
        log.info("Удален пользователь с id {}", id);
    }

    private User checkUserId(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не существует", userId)));
    }
}
