package ru.practicum.shareit.item.dto;

public interface ItemDtoForRequest {
    Long getId();
    String getName();
    String getDescription();
    Boolean getAvailable();
    Long getRequestId();
}
