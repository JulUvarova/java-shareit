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
import ru.practicum.shareit.item.comment.CommentDtoRequest;
import ru.practicum.shareit.item.comment.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private MockMvc mvc;
    private ItemDtoShort itemResponse;
    private ItemDto itemResponseFull;
    private ItemDtoShort itemRequest;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        itemResponse = ItemDtoShort.builder()
                .id(1L)
                .name("Thing")
                .description("about thing...")
                .available(true)
                .requestId(1L)
                .build();

        itemRequest = ItemDtoShort.builder()
                .name("Thing")
                .description("about thing...")
                .available(true)
                .build();

        itemResponseFull = ItemDto.builder()
                .id(1L)
                .name("Thing")
                .description("about thing...")
                .available(true)
                .requestId(1L)
                .lastBooking(null)
                .nextBooking(null)
                .comments(List.of())
                .build();
    }

    @Test
    void createItem_whenValidItem_thenReturnItem() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDtoShort.class)))
                .thenReturn(itemResponse);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemResponse.getRequestId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())));
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
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
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
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
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
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
    }

    @Test
    void updateItem_whenValidItem_thenReturnItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDtoShort.class)))
                .thenReturn(itemResponse);
        long id = 1L;

        mvc.perform(patch("/items/{itemId}", id)
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemResponse.getRequestId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())));
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
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
    }

    @Test
    void getItem_whenValidId_thenReturnItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemResponseFull);
        long id = 1L;

        mvc.perform(get("/items/{itemId}", id)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseFull.getId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemResponseFull.getRequestId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseFull.getName())))
                .andExpect(jsonPath("$.available", is(itemResponseFull.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemResponseFull.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemResponseFull.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemResponseFull.getComments())))
                .andExpect(jsonPath("$.description", is(itemResponseFull.getDescription())));
    }

    @Test
    void getItemsByUser_whenEmptyItemList_thenReturnEmptyList() throws Exception {
        List<ItemDto> items = List.of();

        when(itemService.getItemsByUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items?from=0&size=20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getItemsByUser_whenItemListWithOneItem_thenReturnList() throws Exception {
        List<ItemDto> items = List.of(itemResponseFull);

        when(itemService.getItemsByUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items?from=0&size=20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemResponseFull.getId()), Long.class))
                .andExpect(jsonPath("$[0].requestId", is(itemResponseFull.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseFull.getName())))
                .andExpect(jsonPath("$[0].available", is(itemResponseFull.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemResponseFull.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemResponseFull.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", is(itemResponseFull.getComments())))
                .andExpect(jsonPath("$[0].description", is(itemResponseFull.getDescription())));
    }

    @Test
    void searchItems_whenEmptyItemList_thenReturnEmptyList() throws Exception {
        List<ItemDtoShort> items = List.of(itemResponse);

        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items/search?text=оТверТ&from=0&size=20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].requestId", is(itemResponse.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponse.getName())))
                .andExpect(jsonPath("$[0].available", is(itemResponse.getAvailable())))
                .andExpect(jsonPath("$[0].description", is(itemResponse.getDescription())));
    }

    @Test
    void searchItems_whenItemListWithOneItem_thenReturnList() throws Exception {
        List<ItemDtoShort> items = List.of();

        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items/search?text=оТверТ&from=0&size=20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createComment_whenValidComment_thenReturnComment() throws Exception {
        CommentDtoResponse commentResponse = CommentDtoResponse.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .text("i'm comment")
                .authorName("Owner")
                .build();
        CommentDtoRequest commentRequest = CommentDtoRequest.builder()
                .text("smth")
                .build();
        long id = 1L;

        when(itemService.createComment(anyLong(), anyLong(), any(CommentDtoRequest.class)))
                .thenReturn(commentResponse);

        mvc.perform(post("/items/{itemId}/comment", id)
                        .content(mapper.writeValueAsString(commentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponse.getAuthorName())));
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
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
    }
}