package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserIntegrationTest {
    private final UserService service;

    @Test
    public void getAll() {
        User user = service.save(new User(null, "name1", "name1@yandex.ru"));
        User user2 = service.save(new User(null, "name2", "name2@yandex.ru"));
        User user3 = service.save(new User(null, "name3", "name3@yandex.ru"));

        List<User> userList = service.findAll();
        Assertions.assertEquals(3, userList.size());
        Assertions.assertEquals(user.getName(), userList.get(0).getName());
        Assertions.assertEquals(user2.getName(), userList.get(1).getName());
        Assertions.assertEquals(user3.getName(), userList.get(2).getName());
    }
}
