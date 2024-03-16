package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    UserStorage userStorage;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    private User testUser;

    @BeforeEach
    void init() {
        testUser = User.builder()
                .id(1L)
                .name("UserName")
                .email("old@mail.ru")
                .build();
    }

    @Test
    void createUser_whenValidUser_thenReturnUser() {
        UserDto userToSave = new UserDto();
        User savedUser = new User();
        when(userStorage.save(UserMapper.toUser(userToSave)))
                .thenReturn(savedUser);
        UserDto actualUser = userService.createUser(userToSave);

        assertEquals(userToSave, actualUser);
        verify(userStorage).save(UserMapper.toUser(userToSave));
        verify(userStorage, times(1)).save(UserMapper.toUser(userToSave));
        verify(userStorage, atMost(1)).save(UserMapper.toUser(userToSave));
    }

    @Test
    void updateUserById_whenValidUser_thenReturnUser() {
        long userId = 1L;

        when(userStorage.findById(userId)).thenReturn(Optional.of(testUser));

        UserDto newUser = UserDto.builder()
                .id(userId)
                .name("NewName")
                .email("old@mail.ru")
                .build();

        UserDto actualUserDto = userService.updateUserById(userId, newUser);
        assertEquals(newUser, actualUserDto);

        verify(userStorage).save(userArgumentCaptor.capture());
        User actualUser = userArgumentCaptor.getValue();

        assertEquals("NewName", actualUser.getName());
        assertEquals("old@mail.ru", actualUser.getEmail());
    }

    @Test
    void updateUserById_whenEmptyUserNameAndEmail_thenReturnUser() {
        long userId = 1L;

        when(userStorage.findById(userId)).thenReturn(Optional.of(testUser));

        UserDto newUser = UserDto.builder()
                .id(userId)
                .name("")
                .email("")
                .build();

        userService.updateUserById(userId, newUser);

        verify(userStorage).save(userArgumentCaptor.capture());
        User actualUser = userArgumentCaptor.getValue();

        assertEquals("UserName", actualUser.getName());
        assertEquals("old@mail.ru", actualUser.getEmail());
    }

    @Test
    void updateUserById_whenInvalidUserId_thenReturnException() {
        long userId = 1L;
        User user = new User();

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(userId));

        assertEquals(String.format("Пользователь с id %d не существует", userId), exception.getMessage());
        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, times(0)).save(user);
    }

    @Test
    void getUserById_whenValidUserId_thenReturnUser()  {
        long userId = 1L;
        User expectedUser = new User();

        when(userStorage.findById(userId))
                .thenReturn(Optional.of(expectedUser));
        UserDto actualUser = userService.getUserById(userId);

        assertEquals(UserMapper.toUserDto(expectedUser), actualUser);
        verify(userStorage, times(1)).findById(userId);
    }

    @Test
    void getUserById_whenInvalidUserId_thenReturnException() {
        long userId = 1L;

        when(userStorage.findById(userId))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(userId));

        assertEquals(String.format("Пользователь с id %d не существует", userId), exception.getMessage());
        verify(userStorage, times(1)).findById(userId);
    }

    @Test
    void getAllUsers_whenUserListNotEmpty_thenReturnList() {
        List<User> users = List.of(testUser);

        when(userStorage.findAll()).thenReturn(users);

        int listSize = userService.getAllUsers().size();

        assertEquals(1, listSize);
        verify(userStorage, times(1)).findAll();
    }

    @Test
    void deleteUserById_whenValidUserId_thenDeleteUser() {
        long userId = 1L;
        when(userStorage.findById(userId))
                .thenReturn(Optional.of(testUser));

        userService.deleteUserById(userId);

        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, times(1)).deleteById(userId);
    }

    @Test
    void deleteUserById_whenInvalidUserId_thenReturnException() {
        long userId = 1L;

        when(userStorage.findById(userId))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUserById(userId));

        assertEquals(String.format("Пользователь с id %d не существует", userId), exception.getMessage());
        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, times(0)).deleteById(userId);
    }
}