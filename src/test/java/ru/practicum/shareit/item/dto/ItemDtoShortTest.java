package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
@JsonTest
class ItemDtoShortTest {
    @Autowired
    private JacksonTester<ItemDtoShort> json;

    @Test
    void testItemDtoShort() throws Exception {
        ItemDtoShort dto = ItemDtoShort.builder()
                .id(1L)
                .name("Thing")
                .description("about thing...")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemDtoShort> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Thing");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("about thing...");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}