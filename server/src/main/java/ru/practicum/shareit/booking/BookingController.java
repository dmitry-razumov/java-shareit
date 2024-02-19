package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@ComponentScan(basePackageClasses = BookingMapper.class)
public class BookingController {
    private final BookingService service;
    private final BookingMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("POST /bookings with body {} and X-Sharer-User-Id={} ", bookingRequestDto, userId);
        return mapper.toResponseDto(service.createBooking(mapper.toBooking(bookingRequestDto),
                userId, bookingRequestDto.getItemId()));
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long bookingId,
                                           @RequestParam boolean approved) {
        log.info("PATCH /bookings/{}?approved={} and X-Sharer-User-Id={} ", bookingId, approved, userId);
        return mapper.toResponseDto(service.updateStatus(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId) {
        log.info("GET /bookings/{} and X-Sharer-User-Id={} ", bookingId, userId);
        return mapper.toResponseDto(service.getById(bookingId, userId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) {
        log.info("GET /bookings/?state={}&from={}&size={} and X-Sharer-User-Id={} ",
                state, from, size, userId);
        return mapper.toResponseDto(service.getAllByUser(userId, state, from, size));
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllByOwnerItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "20") int size) {
        log.info("GET /bookings/owner?state={}&from={}&size={} and X-Sharer-User-Id={} ",
                state, from, size, userId);
        return mapper.toResponseDto(service.getAllByOwnerItems(userId, state, from, size));
    }
}
