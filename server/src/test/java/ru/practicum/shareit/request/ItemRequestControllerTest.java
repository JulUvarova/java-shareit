package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private MockMvc mvc;
    private ItemRequestDtoResponse responseDto;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        responseDto = ItemRequestDtoResponse.builder()
                .id(1L)
                .items(List.of())
                .created(LocalDateTime.now())
                .description("desc")
                .requestor(1L)
                .build();
    }

    @Test
    void createItemRequest_whenValidItemRequest_thenReturnItemRequest() throws Exception {
        ItemRequestDtoRequest requestDto = ItemRequestDtoRequest.builder()
                .description("desc")
                .build();

        when(itemRequestService.createRequest(anyLong(), any(ItemRequestDtoRequest.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.items", is(responseDto.getItems())))
                .andExpect(jsonPath("$.requestor", is(responseDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())));
    }

    @Test
    void createItemRequest_whenEmptyItemRequest_thenReturnError() throws Exception {
        ItemRequestDtoRequest invalidRequest = ItemRequestDtoRequest.builder()
                .description("")
                .build();

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemRequestService);
    }

    @Test
    void createItemRequest_whenBigInvalidItemRequest_thenReturnError() throws Exception {
        ItemRequestDtoRequest invalidRequest = ItemRequestDtoRequest.builder()
                .description("Реализовать юнит-тесты для всего кода, содержащего логику. " +
                        "Выберите те классы, которые содержат в себе нетривиальные методы, условия и ветвления. " +
                        "В основном это будут классы сервисов. Напишите юнит-тесты на все такие методы, используя моки при необходимости.\n" +
                        "Реализовать интеграционные тесты, проверяющие взаимодействие с базой данных. Как вы помните, " +
                        "интеграционные тесты представляют собой более высокий уровень тестирования: их обычно требуется " +
                        "меньше, но покрытие каждого — больше. Мы предлагаем вам создать по одному интеграционному тесту" +
                        "для каждого крупного метода в ваших сервисах. Например, для метода getUserItems в классе ItemServiceImpl.")
                .build();

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemRequestService);
    }

    @Test
    void getRequestsById_whenValidId_thenReturnItemRequest() throws Exception {
        long id = 1L;

        when(itemRequestService.getRequestsById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/requests/{requestId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.items", is(responseDto.getItems())))
                .andExpect(jsonPath("$.requestor", is(responseDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())));
    }

    @Test
    void getAllRequests_whenEmptyRequestItemList_thenReturnEmptyList() throws Exception {
        List<ItemRequestDtoResponse> itemRequests = List.of();

        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequests);

        mvc.perform(get("/requests/all?from=0&size=20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllRequests_whenRequestItemListWithOneEntity_thenReturnList() throws Exception {
        List<ItemRequestDtoResponse> itemRequests = List.of(responseDto);

        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequests);

        mvc.perform(get("/requests/all?from=0&size=20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getRequests_whenEmptyRequestItemList_thenReturnEmptyList() throws Exception {
        List<ItemRequestDtoResponse> itemRequests = List.of();

        when(itemRequestService.getRequestsByOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequests);

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getRequests_whenRequestItemListWithOneEntity_thenReturnList() throws Exception {
        List<ItemRequestDtoResponse> itemRequests = List.of(responseDto);

        when(itemRequestService.getRequestsByOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequests);

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}