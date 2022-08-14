package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class Item {
    private Long id;
    @NotBlank(groups = {Update.class})
    private String name;
    @NotBlank(groups = {Update.class})
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
