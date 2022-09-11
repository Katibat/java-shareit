package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking save(Booking booking, Long userId, Long itemId);

    void deleteById(Long bookingId);

    Booking changeBookingStatus(Long userId, Long bookingId, Boolean approved);

    Booking findById(Long userId, Long bookingId);

    List<BookingDto> findAllByBooker(int fromPage, int size, Long userId, String state);

    List<BookingDto> findAllByOwner(int fromPage, int size, Long userId, String state);

    Optional<Booking> findLastBooking(Long itemId);

    Optional<Booking> findNextBooking(Long itemId);
}
