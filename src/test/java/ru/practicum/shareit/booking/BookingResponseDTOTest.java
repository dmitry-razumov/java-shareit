package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingResponseDTOTest {
    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    void shouldConvertToValidJSON() throws IOException {
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
        LocalDateTime start = LocalDateTime.of(LocalDate.of(2024, 02, 15),
                LocalTime.of(06, 03, 28));
        LocalDateTime end = LocalDateTime.of(LocalDate.of(2024, 02, 15),
                LocalTime.of(10, 03, 28));
        Booking booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .booker(booker)
                .item(item)
                .status(Status.WAITING)
                .build();

        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();

        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("owner");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email")
                .isEqualTo("owner@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.item.request").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item.lastBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item.comments").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("booker");
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("booker@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
