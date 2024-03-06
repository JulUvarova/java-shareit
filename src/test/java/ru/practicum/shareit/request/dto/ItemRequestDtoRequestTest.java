package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoRequestTest {
    @Autowired
    private JacksonTester<ItemRequestDtoRequest> json;

    @Test
    void testItemRequestDtoRequest() throws Exception {
        ItemRequestDtoRequest dto = ItemRequestDtoRequest.builder()
                .description("desc")
                .build();

        JsonContent<ItemRequestDtoRequest> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
    }
}