package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Thing")
                .description("about thing...")
                .available(true)
                .requestId(1L)
                .lastBooking(BookingForItemDto.builder()
                        .id(1L)
                        .bookerId(1L)
                        .build())
                .nextBooking(BookingForItemDto.builder()
                        .id(2L)
                        .bookerId(2L)
                        .build())
                .comments(List.of())
                .build();

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Thing");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("about thing...");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEqualTo(List.of());
    }
}