package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserClient userClient;
    @InjectMocks
    private UserController userController;
    private MockMvc mvc;
    private UserDto firstUser;
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
        verifyNoInteractions(userClient);
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
        verifyNoInteractions(userClient);
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
        verifyNoInteractions(userClient);
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
        verifyNoInteractions(userClient);
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
        verifyNoInteractions(userClient);
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
        verifyNoInteractions(userClient);
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
        verifyNoInteractions(userClient);
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
        verifyNoInteractions(userClient);
    }
}