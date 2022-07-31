package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Create;
import ru.practicum.shareit.item.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private final Long id;
    @NotBlank(groups = {Create.class})
    private final String name;
    @Email(groups = {Update.class, Create.class})
    @NotNull(groups = {Create.class})
    private final String email;
}
