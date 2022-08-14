package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private final Long id;
    @NotNull
    @NotBlank
    private final String description;
    private final User requestor;
    @NotNull
    @FutureOrPresent
    private final LocalDateTime created;
}
