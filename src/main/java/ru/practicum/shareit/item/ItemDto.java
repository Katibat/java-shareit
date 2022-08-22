package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    @JsonIgnore
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

    public void setLastBooking(Optional<Booking> booking) {
        if (booking.isPresent()) {
            this.lastBooking  = new BookingDto(
                    booking.get().getId(),
                    booking.get().getBooker().getId(),
                    booking.get().getStart(),
                    booking.get().getEnd()
            );
        }
    }

    public void setNextBooking(Optional<Booking> booking) {
        if (booking.isPresent()) {
            this.nextBooking  = new BookingDto(
                    booking.get().getId(),
                    booking.get().getBooker().getId(),
                    booking.get().getStart(),
                    booking.get().getEnd()
            );
        }
    }
}