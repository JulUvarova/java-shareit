package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CommentStorage extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(long item);

    List<Comment> findAllByItemIdIn(Set<Long> itemId);
}
