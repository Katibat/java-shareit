package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        Long id;
        String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class BookingDto {
        Long id;
        Long bookerId;
        LocalDateTime start;
        LocalDateTime end;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public void setLastBooking(Booking booking) {
        if (booking != null) {
            this.lastBooking  = new BookingDto(
                    booking.getId(),
                    booking.getBooker().getId(),
                    booking.getStart(),
                    booking.getEnd()
            );
        }
    }

    public void setNextBooking(Booking booking) {
        if (booking != null) {
            this.nextBooking  = new BookingDto(
                    booking.getId(),
                    booking.getBooker().getId(),
                    booking.getStart(),
                    booking.getEnd()
            );
        }
    }
}