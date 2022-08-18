package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
//    @Transactional
    public User save(User user) {
        if (user.getEmail() == null || user.getName() == null) {
            throw new ValidationException("Обязательные поля для создания пользователя не заполнены.");
        }
        try {
            return repository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Адрес электронной почты уже используется другим пользователем.");
        }
    }

    @Override
//    @Transactional
    public User update(Long userId, User user) {
        User updateUser = findById(userId);
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        return repository.save(updateUser);
    }

    @Override
//    @Transactional
    public void deleteById(Long userId) {
        repository.deleteById(userId);
    }

    @Override
    public User findById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с идентификатором № " + userId));
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }
}
