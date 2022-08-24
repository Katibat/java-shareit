package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
