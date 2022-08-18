package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    @Email
    private String email;
}
