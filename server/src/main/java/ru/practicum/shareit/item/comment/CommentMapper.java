package ru.practicum.shareit.item.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment toComment(CommentDtoRequest commentDto, User author, long itemId) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(author)
                .itemId(itemId)
                .created(LocalDateTime.now())
                .build();
    }

    public CommentDtoResponse toCommentDtoResponse(Comment comment) {
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
