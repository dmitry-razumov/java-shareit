package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    private long itemId;
    @NotNull
    @FutureOrPresent (message = "Дата начала аренды должна быть не ранее сегодня")
    private LocalDateTime start;
    @NotNull
    @Future (message = "Дата конца аренды должна быть позднее сегодня")
    private LocalDateTime end;
}