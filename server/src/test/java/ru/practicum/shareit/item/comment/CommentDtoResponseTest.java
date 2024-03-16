package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoResponseTest {
    @Autowired
    private JacksonTester<CommentDtoResponse> json;

    @Test
    void testCommentDtoResponse() throws Exception {
        CommentDtoResponse dto = CommentDtoResponse.builder()
                .id(1L)
                .text("Thing")
                .authorName("User")
                .created(LocalDateTime.of(2022, 07, 03, 19, 55, 00))

                .build();

        JsonContent<CommentDtoResponse> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Thing");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("User");
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo("2022-07-03T19:55:00");
    }
}