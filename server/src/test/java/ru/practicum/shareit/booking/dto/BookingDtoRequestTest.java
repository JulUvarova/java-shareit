package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoRequestTest {
    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    @Test
    void testBookingDtoRequest() throws Exception {
        BookingDtoRequest dto = BookingDtoRequest.builder()
                .start(LocalDateTime.of(2022, 07, 03, 19, 55, 00))
                .end(LocalDateTime.of(2023, 07, 03, 19, 55, 00))
                .itemId(1L)
                .build();

        JsonContent<BookingDtoRequest> result = json.write(dto);

        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2022-07-03T19:55:00");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2023-07-03T19:55:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}