package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingDto {
    private long itemId;
    @NotNull(message = "Дата начала аренды не должна быть null")
    @FutureOrPresent (message = "Дата начала аренды должна быть не ранее сегодня")
    private LocalDateTime start;
    @NotNull(message = "Дата конца аренды не должна быть null")
    @Future (message = "Дата конца аренды должна быть позднее сегодня")
    private LocalDateTime end;
}
