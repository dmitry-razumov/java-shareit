package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingResponseDto toResponseDto(Booking booking);

    List<BookingResponseDto> toResponseDto(List<Booking> booking);

    Booking toBooking(BookingRequestDto bookingRequestDto);
}
