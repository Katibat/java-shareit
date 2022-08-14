package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import ru.practicum.shareit.status.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @FutureOrPresent
    private LocalDate start;
    @Future
    private LocalDate end;
    private Item item;
    private User booker;
    private Status status;

    @Getter
    @AllArgsConstructor
    static class Item {
        private Long id;
        private String name;
    }

    @Getter
    @AllArgsConstructor
    static class User {
        private Long id;
        private String name;
    }
}
