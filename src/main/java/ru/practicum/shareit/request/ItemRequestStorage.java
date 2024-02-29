package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {
    Page<ItemRequest> findAllByRequestorId(long userId, PageRequest pageRequest);

    Page<ItemRequest> findAllByRequestorIdNot(long userId, PageRequest pageRequest);
}
