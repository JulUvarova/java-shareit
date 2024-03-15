package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated

public class ItemRequestController {
    private final ItemRequestService requestService;

    @Autowired
    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDtoResponse createItemRequest(@RequestHeader(Constant.OWNER_ID) long userId,
                                                    @RequestBody ItemRequestDtoRequest requestDto) {
        log.info("Получен запрос от пользователя с id {} на создание запроса на вещь", userId);
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getRequests(@RequestHeader(Constant.OWNER_ID) long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "5") Integer size) {
        log.info("Получен запрос от пользователя с id {} на получение списка запросов", userId);
        return requestService.getRequestsByOwner(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getAllRequests(@RequestHeader(Constant.OWNER_ID) long userId,
                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @RequestParam(name = "size", defaultValue = "5") Integer size) {
        log.info("Получен запрос от пользователя с id {} на получение запросов по {} на странице", userId, size);
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getRequestsById(@RequestHeader(Constant.OWNER_ID) long userId,
                                                  @PathVariable long requestId) {
        log.info("Получен запрос от пользователя с id {} на получение запроса с id {}", userId, requestId);
        return requestService.getRequestsById(userId, requestId);
    }
}
