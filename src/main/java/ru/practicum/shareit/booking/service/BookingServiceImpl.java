package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedException;
import ru.practicum.shareit.exception.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User findUserByIdOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Item findItemByIdOrThrow(long ItemId) {
        return itemRepository.findById(ItemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + ItemId + " не найдена"));
    }

    private Booking findBookingByIdOrThrow(long BookingId) {
        return bookingRepository.findById(BookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с id=" + BookingId + " не найдена"));
    }

    @Override
    @Transactional
    public Booking createBooking(Booking booking, long userId, long itemId) {
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new ValidationException("Время окончания бронирования не может быть раньше начала");
        }
        User booker = findUserByIdOrThrow(userId);
        Item item = findItemByIdOrThrow(itemId);
        if (!item.getAvailable()) {
            throw new ValidationException("Item с id=" + itemId + " не доступна для бронирования");
        }
        if (item.getOwner().getId() == booker.getId()) {
            throw new NotFoundException("Пользователь не может бронировать у самого себя");
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("создано бронирование - {}", savedBooking);
        return savedBooking;
    }

    @Override
    @Transactional
    public Booking updateStatus(long userId, long bookingId, boolean approved) {
        User booker = findUserByIdOrThrow(userId);
        Booking booking = findBookingByIdOrThrow(bookingId);
        if (booking.getItem().getOwner().getId() != booker.getId()) {
            throw new NotFoundException("Пользователь с id=" + bookingId + " не является собственником");
        }
        if ((booking.getStatus() == Status.APPROVED && approved)
                || (booking.getStatus() == Status.REJECTED && !approved)) {
            throw new ValidationException("Вещь с id=" + booking.getItem().getId() + " не доступна для бронирования");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        log.info("обновлен статус бронирования - {}", booking);
        return booking;
    }

    @Override
    public Booking getById(long bookingId, long userId) {
        User booker = findUserByIdOrThrow(userId);
        Booking booking = findBookingByIdOrThrow(bookingId);
        if (booking.getBooker().getId() != booker.getId()
                && booking.getItem().getOwner().getId() != booker.getId()) {
            throw new NotFoundException("Пользователь с id=" + booker.getId() + " должен быть либо собственником,"
                    + " либо автором бронирования");
        }
        log.info("получено бронирование - {}", booking);
        return booking;
    }

    @Override
    public List<Booking> getAllByUser(long userId, String state, int from, int size) {
        User booker = findUserByIdOrThrow(userId);
        LocalDateTime currentTime = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size);
        List<Booking> listBooking;
        switch (getState(state)) {
            case ALL:
                listBooking = bookingRepository
                        .findAllByBookerIdOrderByStartDesc(booker.getId(), page);
                break;
            case PAST:
                listBooking = bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), currentTime, page);
                break;
            case CURRENT:
                listBooking = bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(booker.getId(),
                                currentTime, currentTime, page);
                break;
            case FUTURE:
                listBooking = bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), currentTime, page);
                break;
            case WAITING:
                listBooking = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING, page);
                break;
            case REJECTED:
                listBooking = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.REJECTED, page);
                break;
            default:
                throw new UnsupportedException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("получена page from={} size={} с бронированиями - {}", from, size, listBooking);
        return listBooking;
    }

    @Override
    public List<Booking> getAllByOwnerItems(long userId, String state, int from, int size) {
        User owner = findUserByIdOrThrow(userId);
        LocalDateTime currentTime = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size);
        List<Booking> listBooking;
        switch (getState(state)) {
            case ALL:
                listBooking = bookingRepository
                        .findAllByOwnerIdOrderByStartDesc(owner.getId(), page);
                break;
            case PAST:
                listBooking = bookingRepository
                        .findAllByOwnerIdAndEndBeforeOrderByStartDesc(owner.getId(), currentTime, page);
                break;
            case CURRENT:
                listBooking = bookingRepository
                        .findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(owner.getId(),
                                currentTime, currentTime, page);
                break;
            case FUTURE:
                listBooking = bookingRepository
                        .findAllByOwnerIdAndStartAfterOrderByStartDesc(owner.getId(), currentTime, page);
                break;
            case WAITING:
                listBooking = bookingRepository
                        .findAllByOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING, page);
                break;
            case REJECTED:
                listBooking = bookingRepository
                        .findAllByOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED, page);
                break;
            default:
                throw new UnsupportedException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("получена page from={} size={} с бронированиями - {}", from, size, listBooking);
        return listBooking;
    }

    private State getState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
