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
import ru.practicum.shareit.constant.Constant;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingClient bookingClient;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
    }

    @Test
    void createBooking_whenInvalidBookingStart_thenReturnBadRequest() throws Exception {
        BookingDtoRequest invalidBookingRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().minusDays(1L))
                .end(LocalDateTime.now().plusDays(1L))
                .itemId(1L)
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(invalidBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constant.USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }
}