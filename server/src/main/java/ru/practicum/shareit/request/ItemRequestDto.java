package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    private Long requesterId;
    private LocalDateTime created = LocalDateTime.now();
    private List<ItemDto> items;
}