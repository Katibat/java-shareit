package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name; // для прохождения тестов валидацию аннотациями пришлось снять,
    // передаются пустые полями для обновления и код ответа должен быть 200
    @Email
    private String email;
}
