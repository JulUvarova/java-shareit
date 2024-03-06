package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoRequestTest {
    @Autowired
    private JacksonTester<CommentDtoRequest> json;

    @Test
    void testCommentDtoRequest() throws Exception {
        CommentDtoRequest dto = CommentDtoRequest.builder()
                .text("Thing")
                .build();

        JsonContent<CommentDtoRequest> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Thing");
    }
}