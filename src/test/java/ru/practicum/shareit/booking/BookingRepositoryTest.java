package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    static User createOwner() {
        return User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build();
    }

    public static User createBooker() {
        return User.builder()
                .name("booker")
                .email("booker@mail.com")
                .build();
    }

    static Item createItem(User owner) {
        return Item.builder()
                .name("item")
                .description("description")
                .available(Boolean.TRUE)
                .owner(owner)
                .build();
    }

    static Booking createBooking(Item item, User booker) {
        return Booking.builder()
                .status(Status.WAITING)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .build();
    }

    @BeforeEach
    void beforeEach() {
        owner = createOwner();
        booker = createBooker();
        item = createItem(owner);
        booking = createBooking(item, booker);
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);
    }

    @Test
    void shouldFindAllBookingsByBookerId() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), PageRequest.of(0,20));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllBookingsByNoExistBookerId() {
        List<Booking> emptyList = bookingRepository.findAllByBookerIdOrderByStartDesc(99L, PageRequest.of(0,20));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindAllByBookerIdAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(
                booker.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
                PageRequest.of(0,20));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllByBookerIdAndStartBeforeAndEndAfterByNoExistBookerId() {
        List<Booking> emptyList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(
                99L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
                PageRequest.of(0,20));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindAllByBookerIdAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                booker.getId(), LocalDateTime.now().plusDays(2), PageRequest.of(0,20));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllByBookerIdAndEndBeforeByNoExistBookerId() {
        List<Booking> emptyList = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                99L, LocalDateTime.now().plusDays(2), PageRequest.of(0,20));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindAllByBookerIdAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                booker.getId(), LocalDateTime.now().minusHours(1), PageRequest.of(0,20));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllByBookerIdAndStartAfterByNoExistBookerId() {
        List<Booking> emptyList = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                99L, LocalDateTime.now().minusHours(1), PageRequest.of(0,20));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), Status.WAITING, PageRequest.of(0,20));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllByBookerIdAndStatusOrderByStartDescByNoExistBookerId() {
        List<Booking> emptyList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                99L, Status.WAITING, PageRequest.of(0,20));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindAllByBookerIdAndItemIdAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(
                booker.getId(), item.getId(), LocalDateTime.now().plusDays(2));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllByBookerIdAndItemIdAndEndBeforeByNoExistBookerId() {
        List<Booking> emptyList = bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(
                99L, 11L, LocalDateTime.now().plusDays(2));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindAllByOwnerId() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdOrderByStartDesc(
                owner.getId(), PageRequest.of(0,20));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllByOwnerIdByNoExistOwnerId() {
        List<Booking> emptyList = bookingRepository.findAllByOwnerIdOrderByStartDesc(
                99L, PageRequest.of(0,20));
        assertThat(emptyList.size(), equalTo(0));
    }


    @Test
    void shouldFindAllByOwnerIdAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                owner.getId(), LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2),
                PageRequest.of(0,20));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllByOwnerIdAndStartBeforeAndEndAfterByNoExistOwnerId() {
        List<Booking> emptyList = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                99L, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2),
                PageRequest.of(0,20));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindAllByOwnerIdAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(
                owner.getId(), LocalDateTime.now().plusDays(2),
                PageRequest.of(0,20));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllByOwnerIdAndEndBeforeByNoExistOwnerId() {
        List<Booking> emptyList = bookingRepository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(
                99L, LocalDateTime.now().plusDays(2),
                PageRequest.of(0,20));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindAllByOwnerIdAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(
                owner.getId(), LocalDateTime.now().minusMinutes(2),
                PageRequest.of(0,20));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllByOwnerIdAndStartAfterByNoExistOwnerId() {
        List<Booking> emptyList = bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(
                99L, LocalDateTime.now().minusMinutes(2),
                PageRequest.of(0,20));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindAllByOwnerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(
                owner.getId(), Status.WAITING,
                PageRequest.of(0,20));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
    }

    @Test
    void shouldFindNoneAllByOwnerIdAndStatusByNoExistOwnerId() {
        List<Booking> emptyList = bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(
                owner.getId(), Status.APPROVED,
                PageRequest.of(0,20));
        assertThat(emptyList.size(), equalTo(0));
    }
}
