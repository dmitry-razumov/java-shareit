package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedRequestException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
												@Validated @RequestBody BookingDto bookingDto) {
		log.info("POST /bookings with body {} and X-Sharer-User-Id={} ", bookingDto, userId);
		return bookingClient.createBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> approveBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
												 @PathVariable long bookingId,
												 @RequestParam boolean approved) {
		log.info("PATCH /bookings/{}?approved={} and X-Sharer-User-Id={} ", bookingId, approved, userId);
		return bookingClient.approveBookingById(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
												 @PathVariable long bookingId) {
		log.info("GET /bookings/{}  and X-Sharer-User-Id={} ", bookingId, userId);
		return bookingClient.getBookingById(userId, bookingId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getAllBookingByUser(@RequestHeader("X-Sharer-User-Id") long userId,
													  @RequestParam(defaultValue = "ALL") String state,
													  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
													  @RequestParam(defaultValue = "20") @Positive int size) {
		log.info("GET /bookings/?state={}&from={}&size={} and X-Sharer-User-Id={} ",
				state, from, size, userId);
		return bookingClient.getAllBookingsByUser(userId,
				BookingState.from(state).orElseThrow(
						() -> new UnsupportedRequestException("Unknown state: UNSUPPORTED_STATUS")),
				from, size);
	}

	@GetMapping("/owner")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
													 @RequestParam(defaultValue = "ALL") String state,
													 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
													 @RequestParam(defaultValue = "20") @Positive int size) {
		log.info("GET /bookings/owner?state={}&from={}&size={} and X-Sharer-User-Id={} ",
				state, from, size, userId);
		return bookingClient.getAllBookingsByOwner(userId,
				BookingState.from(state).orElseThrow(
						() -> new UnsupportedRequestException("Unknown state: UNSUPPORTED_STATUS")),
				from, size);
	}
}
