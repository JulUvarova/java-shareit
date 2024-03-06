package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class UserDtoForBookingTest {
    @Autowired
    private JacksonTester<UserDtoForBooking> json;

    @Test
    void testUserDtoForBooking() throws Exception {
        UserDtoForBooking dto = UserDtoForBooking.builder()
                .id(1L)
                .build();

        JsonContent<UserDtoForBooking> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }
}