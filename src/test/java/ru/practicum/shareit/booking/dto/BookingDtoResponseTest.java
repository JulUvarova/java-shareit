package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDtoForBooking;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoResponseTest {
    @Autowired
    private JacksonTester<BookingDtoResponse> json;

    @Test
    void testBookingDtoResponse() throws Exception {
        BookingDtoResponse dto = BookingDtoResponse.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .item(ItemDtoForBooking.builder()
                        .id(1L)
                        .name("Thing")
                        .build())
                .booker(UserDtoForBooking.builder()
                        .id(1L)
                        .build())
                .start(LocalDateTime.of(2022, 07, 03, 19, 55, 00))
                .end(LocalDateTime.of(2023, 07, 03, 19, 55, 00))
                .build();

        JsonContent<BookingDtoResponse> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Thing");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2022-07-03T19:55:00");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2023-07-03T19:55:00");
    }
}