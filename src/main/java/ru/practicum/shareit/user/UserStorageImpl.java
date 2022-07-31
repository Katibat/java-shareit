package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.*;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public User create(User user) {
        checkUniqueEmail(user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
            log.debug("Создан пользователь с идентификатором № {}.", user.getId());
            return user;
    }

    @Override
    public User update(Long userId, User updateUser) {
        User user = getById(userId);
        if (updateUser.getName() != null && !updateUser.getName().isBlank()) {
            user.setName(updateUser.getName());
        }
        if (updateUser.getEmail() != null && !updateUser.getEmail().isBlank()) {
            if (!user.getEmail().equals(updateUser.getEmail())) {
                checkUniqueEmail(updateUser.getEmail());
            }
            user.setEmail(updateUser.getEmail());
        }
        log.info("Обновлены данные пользователя с идентификатором № {}.", user.getId());
        return user;
    }

    @Override
    public void delete(Long userId) {
        try {
            getById(userId);
            users.remove(userId);
            log.debug("Удален пользователь с идентификатором {}.", userId);
        } catch (NotFoundException e) {
            log.warn("При удалении пользователя передан не существующий идентификатор № {}.", userId);
            throw new NotFoundException("Указан неверный идентификатор пользователя № " + userId);
        }
    }

    @Override
    public User getById(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь с идентификатором № {} не найден.", userId);
            throw new NotFoundException("Не найден пользователь с идентификатором № " + userId);
        }
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получен список всех пользователей.");
        return new ArrayList<>(users.values());
    }

    private void deleteAll() {
        users.clear();
        idCounter = 0L;
    }

    private Long generateId() {
        idCounter += 1;
        return idCounter;
    }

    private void checkUniqueEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                log.warn("При создании пользователя передан уже используемый адрес электронной почты {}",
                        user.getEmail());
                throw new UserAlreadyExistException("Пользователь с указанной электронной почтой уже создан.");
            }
        }
    }
}
