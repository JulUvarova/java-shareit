package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
public class BookingDtoRequest {
    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;
}
