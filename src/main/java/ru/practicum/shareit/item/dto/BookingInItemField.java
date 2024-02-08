package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
public class BookingInItemField {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    long bookerId;
}
