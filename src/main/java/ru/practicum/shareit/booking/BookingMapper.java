package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(null) // null
                .booker(null) // null
                .status(bookingDto.getStatus())
                .build();
    }

    public static Booking toBookingNew(BookingDtoNew bookingDtoNew) {
        return Booking.builder()
                .id(null) // null
                .start(bookingDtoNew.getStart())
                .end(bookingDtoNew.getEnd())
                .item(null) // null
                .booker(null) // null
                .status(null)
                .build();
    }
}
