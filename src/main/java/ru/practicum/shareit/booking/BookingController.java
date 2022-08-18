package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping // добавить новое бронирование
    public BookingDto create(@Valid @RequestBody BookingDtoNew bookingDtoNew,
                             @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        Booking booking = BookingMapper.toBookingNew(bookingDtoNew);
        return BookingMapper.toBookingDto(service.save(booking, userId, bookingDtoNew.getItemId()));
    }

    @DeleteMapping("/{bookingId}") // удалить бронирование
    public void deleteById(@PathVariable("bookingId") Long bookingId) {
        service.deleteById(bookingId);
    }

    @PatchMapping("/{bookingId}") // подтвердить бронирование
    public BookingDto changeBookingStatus(@PathVariable Long bookingId,
                                          @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @RequestParam Boolean approved) {
        return BookingMapper.toBookingDto(service.changeBookingStatus(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}") // искать бронирование по идентификатору
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        return BookingMapper.toBookingDto(service.findById(userId, bookingId));
    }

    @GetMapping // получить список всех бронирований
    public List<BookingDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return service.findAllByBooker(userId, state)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner") // получить список всех бронирований собственника вещей
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return service.findAllByOwner(userId, state)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
