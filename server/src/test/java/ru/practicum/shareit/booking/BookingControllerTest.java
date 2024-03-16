package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDtoForBooking;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mvc;
    private BookingDtoResponse bookingResponse;
    private BookingDtoRequest bookingRequest;

    @Autowired
    private ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        bookingResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusDays(1L))
                .item(ItemDtoForBooking.builder().id(1L).name("Thing").build())
                .booker(UserDtoForBooking.builder().id(1L).build())
                .status(BookingStatus.WAITING)
                .build();

        bookingRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusDays(1L))
                .itemId(1L)
                .build();
    }

    @Test
    void createBooking_whenValidBooking_thenReturnBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingDtoRequest.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId()), Long.class));
    }

    @Test
    void approvedBooking_whenStatusApprove_thenReturnBooking() throws Exception {
        when(bookingService.approvedBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingResponse);
        long id = 1;

        mvc.perform(patch("/bookings/{bookingId}?approved=true", id)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId()), Long.class));
    }

    @Test
    void getBookingById_whenValidId_thenReturnBooking() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingResponse);
        long id = 1;

        mvc.perform(get("/bookings/{bookingId}", id)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId()), Long.class));
    }

    @Test
    void getSortBookingByUser_whenValidSortState_thenReturnBookingList() throws Exception {
        List<BookingDtoResponse> responseList = List.of(bookingResponse);

        when(bookingService.getSortBookingByUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(responseList);

        mvc.perform(get("/bookings?state=ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getSortBookingByOwner_whenValidSortState_thenReturnBookingList() throws Exception {
        List<BookingDtoResponse> responseList = List.of(bookingResponse);

        when(bookingService.getSortBookingByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(responseList);

        mvc.perform(get("/bookings/owner?state=ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.OWNER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}