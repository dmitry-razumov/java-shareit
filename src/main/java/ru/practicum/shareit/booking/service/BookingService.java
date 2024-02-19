package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking, long userId, long itemId);

    Booking updateStatus(long userId, long id, boolean approved);

    Booking getById(long id, long userId);

    List<Booking> getAllByUser(long userId, String state, int from, int size);

    List<Booking> getAllByOwnerItems(long userId, String state, int from, int size);
}
