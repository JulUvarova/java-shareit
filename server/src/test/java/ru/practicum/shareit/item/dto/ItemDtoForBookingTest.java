package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoForBookingTest {
    @Autowired
    private JacksonTester<ItemDtoForBooking> json;

    @Test
    void testItemDtoForBooking() throws Exception {
        ItemDtoForBooking dto = ItemDtoForBooking.builder()
                .id(1L)
                .name("Thing")
                .build();

        JsonContent<ItemDtoForBooking> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Thing");
    }
}