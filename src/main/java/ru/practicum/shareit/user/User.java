package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Create;
import ru.practicum.shareit.item.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank(groups = {Update.class})
    private String name;
    @NotBlank(groups = {Update.class})
    @Email(groups = {Update.class, Create.class})
    private String email;
}