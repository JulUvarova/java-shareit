package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;

import java.util.List;
import java.util.Set;

public interface ItemStorage extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " +
            " where i.available is true and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) ")
    Page<Item> search(String text, PageRequest pageRequest);

    Page<Item> findAllItemsByOwnerId(long userId, PageRequest pageRequest);

    List<ItemDtoForRequest> findAllByRequestIdIn(Set<Long> requestId);

    List<ItemDtoForRequest> findAllByRequestId(long requestId);
}
