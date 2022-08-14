package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotNull
    @NotBlank
    private String description;
    private User requestor;
    @NotNull
    @FutureOrPresent
    private LocalDateTime created;

    @Getter
    @AllArgsConstructor
    static class User {
        private Long id;
        private String name;
    }
}
