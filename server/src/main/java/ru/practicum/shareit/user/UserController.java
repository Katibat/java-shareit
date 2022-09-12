package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;

    @PostMapping // добавить новых пользователей
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(service.save(user));
    }

    @PatchMapping("/{userId}") // редактировать данные пользователя
    public UserDto update(@PathVariable Long userId,
                       @Valid @RequestBody UserDto userDto) {
        userDto.setId(userId);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(service.update(userId, user));
    }

    @DeleteMapping("/{userId}") // удалить пользователя
    public void deleteById(@PathVariable Long userId) {
        service.deleteById(userId);
    }

    @GetMapping("/{userId}") // искать пользователя по идентификатору
    public UserDto findById(@PathVariable Long userId) {
        return UserMapper.toUserDto(service.findById(userId));
    }

    @GetMapping // получить список всех пользователей
    public List<UserDto> findAll() {
        return service.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}