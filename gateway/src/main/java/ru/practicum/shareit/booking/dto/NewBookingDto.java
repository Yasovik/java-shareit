package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewBookingDto {
    @NotNull(message = "Дата старта брони должна быть установлена")
    @FutureOrPresent(message = "Дата старта брони не может быть в прошлом")
    LocalDateTime start;

    @NotNull(message = "Дата окончания брони должна быть установлена")
    @Future(message = "Дата окончания брони должна быть в будущем")
    LocalDateTime end;

    @NotNull(message = "Необходим идентификатор вещи")
    Long itemId;
}
