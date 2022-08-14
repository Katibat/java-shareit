package ru.practicum.shareit.user;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(Long userId, User updateUser);

    void delete(Long userId);

    User getById(Long userId);

    List<User> getAllUsers();
}
