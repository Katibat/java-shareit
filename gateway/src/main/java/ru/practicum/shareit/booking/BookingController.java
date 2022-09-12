package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                     @RequestParam(name = "state", defaultValue = "all") String state,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                         Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10")
                                                         Integer size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get users bookings, userId={}", userId);
        return bookingClient.getAllUserBookings(from, size, userId, bookingState);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                      @RequestParam(name = "state", defaultValue = "all") String state,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                          Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10")
                                                          Integer size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get owner bookings, userId={}", userId);
        return bookingClient.getAllOwnerBookings(from, size, userId, bookingState);
    }

    @PostMapping
    public ResponseEntity<Object> addBookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.addBookingItem(userId, requestDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam boolean approved) {
        log.info("Updating booking {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.updateBookingItem(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}