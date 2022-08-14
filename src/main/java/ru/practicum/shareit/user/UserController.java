package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Create;
import ru.practicum.shareit.item.Update;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping // добавить новых пользователей
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(service.create(user));
    }

    @PatchMapping("/{id}") // редактировать данные пользователя
    public UserDto update(@PathVariable("id") Long userId,
                       @Validated(Update.class) @RequestBody UserDto userDto) {
        User updateUser = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(service.update(userId, updateUser));
    }

    @DeleteMapping("/{id}") // удалить пользователя
    public void deleteUserById(@PathVariable("id") Long userId) {
        service.delete(userId);
    }

    @GetMapping("/{id}") // искать пользователя по идентификатору
    public UserDto getUserById(@PathVariable("id") Long userId) {
        return UserMapper.toUserDto(service.getById(userId));
    }

    @GetMapping // получить список всех пользователей
    public List<UserDto> getAllUsers() {
        return service.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
