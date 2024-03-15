package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoResponseTest {
    @Autowired
    private JacksonTester<ItemRequestDtoResponse> json;

    @Test
    void testItemRequestDtoResponse() throws Exception {
        ItemRequestDtoResponse dto = ItemRequestDtoResponse.builder()
                .id(1L)
                .items(List.of())
                .created(LocalDateTime.of(2022, 07, 03, 19, 55, 00))
                .description("desc")
                .requestor(1L)
                .build();

        JsonContent<ItemRequestDtoResponse> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(List.of());
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo("2022-07-03T19:55:00");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(1);
    }
}