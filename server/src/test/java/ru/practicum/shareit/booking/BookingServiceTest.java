package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.PageRequestCustom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BookingServiceTest {
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRepository itemRepository;
    @Autowired
    BookingService bookingService;
    private Booking booking;
    private User booker;
    private Item item;

    static Booking createBooking() {
        User booker = User.builder()
                .id(1)
                .name("booker")
                .email("booker@mail.com")
                .build();
        User owner = User.builder()
                .id(2)
                .name("owner")
                .email("owner@mail.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("item")
                .description("description")
                .owner(owner)
                .available(Boolean.TRUE)
                .build();
        return Booking.builder()
                .id(1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(3))
                .booker(booker)
                .item(item)
                .status(Status.WAITING)
                .build();
    }

    @BeforeEach
    void beforeEach() {
        booking = createBooking();
        booker = booking.getBooker();
        item = booking.getItem();
    }

    @Test
    void shouldCreateBooking() {
        when(bookingRepository.save(any()))
                .thenAnswer(invocationOnMock -> {
                    Booking newBooking = invocationOnMock.getArgument(0);
                    newBooking.setId(1L);
                    return newBooking;
                    });
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Booking result = bookingService.createBooking(
                Booking.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(11))
                .build(), item.getId(), booker.getId());
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getId(), equalTo(result.getId()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
        assertThat(booking.getBooker(), equalTo(booker));
    }

    @Test
    void shouldNotCreateBookingWithoutUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(booking, item.getId(), booker.getId()));
        assertThat(exception.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    void shouldNotCreateBookingWithoutItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(booking, item.getId(), booker.getId()));
        assertThat(exception.getMessage(), equalTo("Вещь с id=1 не найдена"));
    }

    @Test
    void shouldNotCreateBookingWithWrongDateTime() {
        booking.setStart(booking.getEnd().plusMinutes(1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booking, item.getId(), booker.getId()));
        assertThat(exception.getMessage(), equalTo("Время окончания бронирования не может быть раньше начала"));
    }

    @Test
    void shouldNotCreateBookingWithUnavailableItem() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booking, item.getId(), booker.getId()));
        assertThat(exception.getMessage(), equalTo("Item с id=1 не доступна для бронирования"));
    }

    @Test
    void shouldNotCreateBookingByEqualUserAndBookerId() {
        item.getOwner().setId(booker.getId());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(booking, item.getId(), booker.getId()));
        assertThat(exception.getMessage(), equalTo("Пользователь не может бронировать у самого себя"));
    }

    @Test
    void shouldApproveBooking() {
        item.getOwner().setId(booker.getId());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        bookingService.updateStatus(booker.getId(), booking.getId(), true);
        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void shouldRejectBooking() {
        item.getOwner().setId(booker.getId());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        bookingService.updateStatus(booker.getId(), booking.getId(), false);
        assertThat(booking.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void shouldNotApproveBookingWithoutBookingEntity() {
        item.getOwner().setId(booker.getId());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.updateStatus(booker.getId(), booking.getId(), true));
        assertThat(exception.getMessage(), equalTo("Бронирования с id=1 не найдена"));
    }

    @Test
    void shouldNotApproveBookingByNotItemOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.updateStatus(booker.getId(), booking.getId(), true));
        assertThat(exception.getMessage(), equalTo("Пользователь с id=1 не является собственником"));
    }

    @Test
    void shouldNotApproveBookingWhenStatusApproved() {
        item.getOwner().setId(booker.getId());
        booking.setStatus(Status.APPROVED);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.updateStatus(booker.getId(), booking.getId(), true));
        assertThat(exception.getMessage(), equalTo("Вещь с id=1 не доступна для бронирования"));
    }

    @Test
    void shouldNotApproveBookingWhenStatusRejected() {
        item.getOwner().setId(booker.getId());
        booking.setStatus(Status.REJECTED);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.updateStatus(booker.getId(), booking.getId(), false));
        assertThat(exception.getMessage(), equalTo("Вещь с id=1 не доступна для бронирования"));
    }

    @Test
    void shouldGetBookingById() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Booking result = bookingService.getById(booker.getId(), booking.getId());
        assertThat(result, equalTo(booking));
    }

    @Test
    void shouldNotGetBookingWithoutBookingEntity() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(booker.getId(), booking.getId()));
        assertThat(exception.getMessage(), equalTo("Бронирования с id=1 не найдена"));
    }

    @Test
    void shouldNotGetBookingWithoutBooker() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(booker.getId(), booking.getId()));
        assertThat(exception.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    void shouldNotGetBookingWhenUserIdNotEqualBookerId() {
        User user = User.builder()
                .id(3L)
                .name("user")
                .email("user@mail.com")
                .build();
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(3L, booking.getId()));
        assertThat(exception.getMessage(),
                equalTo("Пользователь с id=3 должен быть либо собственником, либо автором бронирования"));
    }

    @Test
    void shouldGetAllBookingsByStatuses() {
        Pageable page = PageRequestCustom.get(0, 3);
        List<Booking> bookings = List.of(booking);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), page))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(anyLong(), any(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING, page))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.REJECTED, page))
                .thenReturn(bookings);
        List<Booking> all = bookingService.getAllByUser(booker.getId(), "ALL", 0, 3);
        List<Booking> past = bookingService.getAllByUser(booker.getId(), "PAST", 0, 3);
        List<Booking> current = bookingService.getAllByUser(booker.getId(), "CURRENT", 0, 3);
        List<Booking> future = bookingService.getAllByUser(booker.getId(), "FUTURE", 0, 3);
        List<Booking> waiting = bookingService.getAllByUser(booker.getId(), "WAITING", 0, 3);
        List<Booking> rejected = bookingService.getAllByUser(booker.getId(), "REJECTED", 0, 3);

        assertThat(all.get(0), equalTo(booking));
        assertThat(past.get(0), equalTo(booking));
        assertThat(current.get(0), equalTo(booking));
        assertThat(future.get(0), equalTo(booking));
        assertThat(waiting.get(0), equalTo(booking));
        assertThat(rejected.get(0), equalTo(booking));
    }

    @Test
    void shouldExceptionForGetAllBookingsWhenUnsupportedStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        UnsupportedException exception = assertThrows(UnsupportedException.class, () ->
                bookingService.getAllByUser(booker.getId(), "Unknown", 0, 3));
        assertThat(exception.getMessage(), equalTo("Unknown state: UNSUPPORTED_STATUS"));
    }

    @Test
    void shouldGetAllBookingsByOwnerIdByStatutes() {
        Pageable page = PageRequestCustom.get(0, 3);
        List<Booking> bookings = List.of(booking);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByOwnerIdOrderByStartDesc(booker.getId(), page))
                .thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING, page))
                .thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(booker.getId(), Status.REJECTED, page))
                .thenReturn(bookings);
        List<Booking> all = bookingService.getAllByOwnerItems(booker.getId(), "ALL", 0, 3);
        List<Booking> past = bookingService.getAllByOwnerItems(booker.getId(), "PAST", 0, 3);
        List<Booking> current = bookingService.getAllByOwnerItems(booker.getId(), "CURRENT", 0, 3);
        List<Booking> future = bookingService.getAllByOwnerItems(booker.getId(), "FUTURE", 0, 3);
        List<Booking> waiting = bookingService.getAllByOwnerItems(booker.getId(), "WAITING", 0, 3);
        List<Booking> rejected = bookingService.getAllByOwnerItems(booker.getId(), "REJECTED", 0, 3);

        assertThat(all.get(0), equalTo(booking));
        assertThat(past.get(0), equalTo(booking));
        assertThat(current.get(0), equalTo(booking));
        assertThat(future.get(0), equalTo(booking));
        assertThat(waiting.get(0), equalTo(booking));
        assertThat(rejected.get(0), equalTo(booking));
    }

    @Test
    void shouldExceptionByGetAllBookingsByOwnerIdByUnsupportedStatus() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        UnsupportedException exception = assertThrows(UnsupportedException.class, () ->
                bookingService.getAllByOwnerItems(booker.getId(), "Unknown", 0, 3));
        assertThat(exception.getMessage(), equalTo("Unknown state: UNSUPPORTED_STATUS"));
    }
}
