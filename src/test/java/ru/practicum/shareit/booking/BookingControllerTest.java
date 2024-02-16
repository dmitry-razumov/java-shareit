package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    static BookingRequestDto createBookingRequest() {
        return BookingRequestDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(11))
                .build();
    }

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

    @Test
    @SneakyThrows
    void shouldCreateBooking() {
        Booking booking = createBooking();
        BookingRequestDto requestDto = createBookingRequest();
        when(bookingService.createBooking(any(), anyLong(), anyLong()))
                .thenReturn(booking);
        String json = mapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.item.id").value(1),
                        jsonPath("$.item.name").value("item"),
                        jsonPath("$.status").value("WAITING"),
                        jsonPath("$.booker.id").value(1),
                        jsonPath("$.booker.name").value("booker"),
                        jsonPath("$.start").isNotEmpty(),
                        jsonPath("$.end").isNotEmpty()
                );
    }

    @Test
    @SneakyThrows
    void shouldNotCreateBookingWithoutUserId() {
        Booking booking = createBooking();
        BookingRequestDto requestDto = createBookingRequest();
        when(bookingService.createBooking(any(), anyLong(), anyLong()))
                .thenReturn(booking);
        String json = mapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
        verify(bookingService, never()).createBooking(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void shouldGetBookingById() {
        Booking booking = createBooking();
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(booking);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.item.id").value(1),
                        jsonPath("$.item.name").value("item"),
                        jsonPath("$.status").value("WAITING"),
                        jsonPath("$.booker.id").value(1),
                        jsonPath("$.booker.name").value("booker"),
                        jsonPath("$.start").isNotEmpty(),
                        jsonPath("$.end").isNotEmpty()
                );
    }

    @Test
    @SneakyThrows
    void shouldNotGetBookingByIdWithNullPath() {
        Booking booking = createBooking();
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(booking);
        mockMvc.perform(get("/bookings/null")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(bookingService, never()).getById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void shouldApproveBooking() {
        Booking booking = createBooking();
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.item.id").value(1),
                        jsonPath("$.item.name").value("item"),
                        jsonPath("$.status").value("WAITING"),
                        jsonPath("$.booker.id").value(1),
                        jsonPath("$.booker.name").value("booker"),
                        jsonPath("$.start").isNotEmpty(),
                        jsonPath("$.end").isNotEmpty()
                );
    }

    @Test
    @SneakyThrows
    void shouldNotApproveBookingWithoutUserId() {
        Booking booking = createBooking();
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);
        mockMvc.perform(patch("/bookings/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().is5xxServerError());
        verify(bookingService, never()).updateStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void shouldGetAllBookings() {
        List<Booking> bookings = List.of(createBooking());
        when(bookingService.getAllByUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);
        mockMvc.perform(get("/bookings")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(bookings.get(0).getId()), Long.class)
                );
    }

    @Test
    @SneakyThrows
    void shouldNotGetAllBookingsWithoutUserId() {
        List<Booking> bookings = List.of(createBooking());
        when(bookingService.getAllByUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);
        mockMvc.perform(get("/bookings")
                        .param("state", "WAITING")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(bookingService, never()).getAllByUser(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void shouldGetAllBookingsByOwnerItems() {
        List<Booking> bookings = List.of(createBooking());
        when(bookingService.getAllByOwnerItems(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(bookings.get(0).getId()), Long.class)
                );
    }

    @Test
    @SneakyThrows
    void shouldNotGetAllBookingsByOwnerItemsWithoutUserId() {
        List<Booking> bookings = List.of(createBooking());
        when(bookingService.getAllByOwnerItems(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "WAITING")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(bookingService, never()).getAllByOwnerItems(anyLong(), any(), anyInt(), anyInt());
    }
}
