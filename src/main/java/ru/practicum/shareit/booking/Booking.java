package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Booking {
    private final Long id;
    @NotNull
    @FutureOrPresent
    private final LocalDate start;
    @NotNull
    @Future
    private final LocalDate end;
    private final Item item;
    private final User booker;
    @NotNull
    private final Status status;
}
