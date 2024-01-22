package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.annotation.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@StartBeforeEndDateValid
@Data
@NoArgsConstructor
@SuperBuilder
public class BookingDtoRequest {
    @FutureOrPresent(message = "Дата бронирования не может быть в прошлом")
    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull(message = "Нельзя забронировать несуществующую вещь")
    private Long itemId;
}
