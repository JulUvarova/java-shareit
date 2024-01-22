package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " +
            " where i.available is true and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) ")
    List<Item> search(String text);

    List<Item> findAllItemsByOwnerId(long userId);
}
