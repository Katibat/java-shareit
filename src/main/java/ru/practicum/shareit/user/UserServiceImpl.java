package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public User create(User user) {
        return storage.create(user);
    }

    @Override
    public User update(Long userId, User user) {
        return storage.update(userId, user);
    }

    @Override
    public void delete(Long userId) {
        storage.delete(userId);
    }

    @Override
    public User getById(Long userId) {
        return storage.getById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }
}
