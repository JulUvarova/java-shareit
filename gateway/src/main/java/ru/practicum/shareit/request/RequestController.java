package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(Constant.USER_ID) long userId,
                                                    @Valid @RequestBody ItemRequestDtoRequest requestDto) {
        log.info("Получен запрос от пользователя с id {} на создание запроса на вещь", userId);
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(Constant.USER_ID) long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(name = "size", defaultValue = "5") @Positive Integer size) {
        log.info("Получен запрос от пользователя с id {} на получение списка запросов", userId);
        return requestClient.getRequestsByOwner(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(Constant.USER_ID) long userId,
                                                       @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(name = "size", defaultValue = "5") @Positive Integer size) {
        log.info("Получен запрос от пользователя с id {} на получение запросов по {} на странице", userId, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestsById(@RequestHeader(Constant.USER_ID) long userId,
                                                  @PathVariable long requestId) {
        log.info("Получен запрос от пользователя с id {} на получение запроса с id {}", userId, requestId);
        return requestClient.getRequestsById(userId, requestId);
    }
}
