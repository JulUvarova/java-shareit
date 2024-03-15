package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private MockMvc mvc;
    private UserDto firstUser;
    private UserDto secondUser;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        firstUser = UserDto.builder()
                .id(1L)
                .name("First_User")
                .email("first@mail.ru")
                .build();

        secondUser = UserDto.builder()
                .id(2L)
                .name("Second_User")
                .email("second@mail.ru")
                .build();
    }

    @Test
    void getAllUsers_whenFewUsers_thenReturnUsers() throws Exception {
        List<UserDto> users = List.of(firstUser, secondUser);

        when(userService.getAllUsers())
                .thenReturn(users);

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(firstUser.getName())))
                .andExpect(jsonPath("$[0].email", is(firstUser.getEmail())))
                .andExpect(jsonPath("$[1].id", is(secondUser.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(secondUser.getName())))
                .andExpect(jsonPath("$[1].email", is(secondUser.getEmail())));

    }

    @Test
    void getAllUsers_whenEmptyUsersList_thenReturnEmptyList() throws Exception {
        List<UserDto> users = List.of();

        when(userService.getAllUsers())
                .thenReturn(users);

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createUser_whenAnyUser_thenReturnUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(firstUser);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())));
    }

    @Test
    void createUser_whenEmptyUserName_thenReturnException() throws Exception {
        firstUser.setName("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService);
    }

    @Test
    void createUser_whenLargestUserName_thenReturnException() throws Exception {
        firstUser.setName("Пабло Диего Хозе Франциско де Паула Хуан Непомукено Криспин Криспиано де ла Сантисима Тринидад Руиз Пикассо");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenEmptyEmail_thenReturnException() throws Exception {
        firstUser.setEmail("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService);
    }

    @Test
    void createUser_whenLargestEmail_thenReturnException() throws Exception {
        firstUser.setEmail("contact-admin-hello-webmaster-info-services-peter-crazy-but-oh-so-ubber-cool-english-alphabet-loverer-abcdefghijklmnopqrstuvwxyz@please-try-to.send-me-an-email-if-you-can-possibly-begin-to-remember-this-coz.this-is-the-longest-email-address-known-to-man-but-to-be-honest.this-is-such-a-stupidly-long-sub-domain-it-could-go-on-forever.pacraig.com");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenInvalidEmailPattern_thenReturnException() throws Exception {
        firstUser.setEmail("@user.mail!&");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_whenAnyUser_thenReturnUser() throws Exception {
        firstUser.builder().name("UpdateName").email("update@mail.ru").build();

        when(userService.updateUserById(1L, firstUser))
                .thenReturn(firstUser);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())));
    }

    @Test
    void updateUser_whenLargestUserName_thenReturnException() throws Exception {
        firstUser.setName("Пабло Диего Хозе Франциско де Паула Хуан Непомукено Криспин Криспиано де ла Сантисима Тринидад Руиз Пикассо");

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService);
    }

    @Test
    void updateUser_whenLargestEmail_thenReturnException() throws Exception {
        firstUser.setEmail("contact-admin-hello-webmaster-info-services-peter-crazy-but-oh-so-ubber-cool-english-alphabet-loverer-abcdefghijklmnopqrstuvwxyz@please-try-to.send-me-an-email-if-you-can-possibly-begin-to-remember-this-coz.this-is-the-longest-email-address-known-to-man-but-to-be-honest.this-is-such-a-stupidly-long-sub-domain-it-could-go-on-forever.pacraig.com");

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService);
    }

    @Test
    void updateUser_whenInvalidEmailPattern_thenReturnException() throws Exception {
        firstUser.setEmail("@user.mail!&");

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_whenAnyId_thenReturnUser() throws Exception {
        long id = 1L;

        when(userService.getUserById(id))
                .thenReturn(firstUser);

        mvc.perform(get("/users/{userId}", id)
                            .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())));
    }

    @Test
    void deleteUser_whenAnyId_thenReturnOk() throws Exception {
        long id = 1L;

        mvc.perform(delete("/users/{userId}", id))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById(id);
    }
}