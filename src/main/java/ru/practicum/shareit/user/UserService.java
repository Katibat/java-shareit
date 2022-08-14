package ru.practicum.shareit.user;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {

    User create(User userDto);

    User update(Long userId, User user);

    void delete(Long userId);

    User getById(Long userId);

    List<User> getAllUsers();
}
