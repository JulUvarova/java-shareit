package ru.practicum.shareit.item;

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
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import java.nio.charset.StandardCharsets;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemClient itemClient;
    @InjectMocks
    private ItemController itemController;
    private MockMvc mvc;
    private ItemDtoShort itemRequest;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        itemRequest = ItemDtoShort.builder()
                .name("Thing")
                .description("about thing...")
                .available(true)
                .build();
    }

    @Test
    void createItem_whenInvalidItemName_thenReturnBadRequest() throws Exception {
        itemRequest = ItemDtoShort.builder()
                .name("")
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void createItem_whenInvalidItemDescription_thenReturnBadRequest() throws Exception {
        itemRequest = ItemDtoShort.builder()
                .description("")
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void createItem_whenInvalidItemAvailable_thenReturnBadRequest() throws Exception {
        itemRequest = ItemDtoShort.builder()
                .available(null)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void updateItem_whenInvalidItemDescription_thenReturnBadRequest() throws Exception {
        itemRequest = ItemDtoShort.builder()
                .description("Реализовать юнит-тесты для всего кода, содержащего логику. \" +\n" +
                        "Выберите те классы, которые содержат в себе нетривиальные методы, условия и ветвления. \" +\n" +
                        "В основном это будут классы сервисов. Напишите юнит-тесты на все такие методы, используя моки при необходимости.\\n\" +\n" +
                        "Реализовать интеграционные тесты, проверяющие взаимодействие с базой данных. Как вы помните, \" +\n" +
                        "интеграционные тесты представляют собой более высокий уровень тестирования: их обычно требуется \" +\n" +
                        "меньше, но покрытие каждого — больше. Мы предлагаем вам создать по одному интеграционному тесту\" +\n" +
                        "для каждого крупного метода в ваших сервисах. Например, для метода getUserItems в классе ItemServiceImpl.")
                .build();
        long id = 1L;

        mvc.perform(patch("/items/{itemId}", id)
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }

    @Test
    void createComment_whenInvalidCommentSize_thenReturnBadRequest() throws Exception {
        CommentDtoRequest commentRequest = CommentDtoRequest.builder()
                .text("")
                .build();
        long id = 1L;

        mvc.perform(post("/items/{itemId}/comment", id)
                        .content(mapper.writeValueAsString(commentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemClient);
    }
}