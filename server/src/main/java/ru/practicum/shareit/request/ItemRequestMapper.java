package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    ItemRequest toItemRequest(User user, ItemRequestDtoRequest request) {
        return ItemRequest.builder()
                .requestor(user)
                .created(LocalDateTime.now())
                .description(request.getDescription())
                .build();
    }

    ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest request, List<ItemDtoForRequest> items) {
        return ItemRequestDtoResponse.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .requestor(request.getRequestor().getId())
                .items(items)
                .build();
    }
}
