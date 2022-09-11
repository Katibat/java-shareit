package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    @Mock
    private UserRepository repository;
    private UserService service;
    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("name@yandex.ru")
            .build();

    @BeforeEach
    void beforeEach() {
        service = new UserServiceImpl(repository);
    }

    @Test
    void saveUserTest() {
        Mockito.when(repository.save(user)).thenReturn(user);
        User result = service.save(user);
        Mockito.verify(repository, Mockito.times(1)).save(user);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(user, result);
    }

    @Test
    void saveUserWithDublicateEmailTest() {
        Mockito.when(repository.save(Mockito.any())).thenThrow(DataIntegrityViolationException.class);
        Assertions.assertThrows(ConflictException.class, () -> service.save(user));
        Mockito.verify(repository, Mockito.times(1)).save(user);
    }

    @Test
    void updateUserTest() {
        User toUpdate = new User(null, "name1Update", "name1Update@yandex.ru");
        User updated = new User(1L, "name1Update", "name1Update@yandex.ru");
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(repository.save(updated)).thenReturn(updated);
        User result = service.update(1L, toUpdate);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(updated, result);
    }

    @Test
    void updateUserWithIncorrectIdTest() {
        User userUpdate = new User(null, "nameUpdate", "nameUpdate@yandex.ru");
        Assertions.assertThrows(NotFoundException.class, () -> service.update(78L, userUpdate));
    }

    @Test
    void updateUserTestWithBlankNameAndEmail() {
        User user1 = new User(null, " ", " ");
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(repository.save(user)).thenReturn(user);
        User result = service.update(1L, user1);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(user, result);
    }

    @Test
    void deleteUserTest() {
        service.deleteById(1L);
        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deleteUserIncorrectIdTest() {
        Mockito.doThrow(NotFoundException.class).when(repository).deleteById(Mockito.anyLong());
        Assertions.assertThrows(NotFoundException.class, () -> service.deleteById(1L));
        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void findUserByIdTest() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        User result = service.findById(1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(user, result);
    }

    @Test
    void findUserByIncorrectIdTest() {
        Mockito.when(repository.findById(18L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(18L));
    }

    @Test
    void findAllUsersTest() {
        Mockito.when(repository.findAll()).thenReturn(List.of(user));
        List<User> result = service.findAll();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(user, result.get(0));
    }
}