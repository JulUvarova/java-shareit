package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.pagination.Paginator;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemRequestService {
    private final ItemRequestStorage requestStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public ItemRequestService(ItemRequestStorage requestStorage, UserStorage userStorage, ItemStorage itemStorage) {
        this.requestStorage = requestStorage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Transactional
    public ItemRequestDtoResponse createRequest(long userId, ItemRequestDtoRequest requestDto) {
        User user = checkUserId(userId);
        ItemRequest requestDtoResponse = requestStorage.save(ItemRequestMapper.toItemRequest(user, requestDto));
        log.info("Пользователь {} создал запрос {}", userId, requestDtoResponse.getId());
        return ItemRequestMapper.toItemRequestDtoResponse(requestDtoResponse, List.of());
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDtoResponse> getRequestsByOwner(long userId, Integer from, Integer size) {
        checkUserId(userId);

        Map<Long, ItemRequest> requests = requestStorage.findAllByRequestorId(userId, Paginator.withSort(from, size, Constant.SORT_BY_CREATED_DESC))
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        Map<Long, List<ItemDtoForRequest>> items = itemStorage.findAllByRequestIdIn(requests.keySet())
                .stream()
                .collect(Collectors.groupingBy(ItemDtoForRequest::getRequestId));

        List<ItemRequestDtoResponse> itemsRequest = requests.values()
                .stream()
                .map(i -> ItemRequestMapper.toItemRequestDtoResponse(i, items.getOrDefault(i.getId(), List.of())))
                .collect(Collectors.toList());
        log.info("Пользователь {} получил список своих запросов из {} элементов", userId, itemsRequest.size());
        return itemsRequest;
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDtoResponse> getAllRequests(long userId, Integer from, Integer size) {
        checkUserId(userId);

        Map<Long, ItemRequest> requests = requestStorage.findAllByRequestorIdNot(userId, Paginator.withSort(from, size, Constant.SORT_BY_CREATED_DESC))
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        Map<Long, List<ItemDtoForRequest>> items = itemStorage.findAllByRequestIdIn(requests.keySet())
                .stream()
                .collect(Collectors.groupingBy(ItemDtoForRequest::getRequestId));

        List<ItemRequestDtoResponse> itemsRequest = requests.values()
                .stream()
                .map(i -> ItemRequestMapper.toItemRequestDtoResponse(i, items.getOrDefault(i.getId(), List.of())))
                .collect(Collectors.toList());
        log.info("Пользователь {} получил список всех запросов из {} элементов", userId, itemsRequest.size());
        return itemsRequest;
    }

    @Transactional(readOnly = true)
    public ItemRequestDtoResponse getRequestsById(long userId, long requestId) {
        checkUserId(userId);
        ItemRequest reqItem = checkRequestId(requestId);
        List<ItemDtoForRequest> items = itemStorage.findAllByRequestId(requestId);

        log.info("Пользователь {} получил запрос с id {}", userId, requestId);
        return ItemRequestMapper.toItemRequestDtoResponse(reqItem, items);
    }

    private User checkUserId(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не существует", userId)));
    }

    private ItemRequest checkRequestId(long reqId) {
        return requestStorage.findById(reqId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id %d не существует", reqId)));
    }
}
