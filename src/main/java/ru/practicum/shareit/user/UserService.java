package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    User save(User user);

    User update(Long userId, User user);

    void deleteById(Long userId);

    User findById(Long userId);

    List<User> findAll();
}
