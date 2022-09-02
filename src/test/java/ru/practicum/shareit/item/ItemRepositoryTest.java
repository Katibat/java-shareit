package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager manager;
    @Autowired
    private ItemRepository repository;
    private final User userNew = new User(null, "user", "user@yandex.ru");
    private final User user = new User(1L, "user", "user@yandex.ru");
    private final ItemRequest itemRequestNew = new ItemRequest(null, "itemRequest",
            user, LocalDateTime.MIN);
    private final ItemRequest itemRequest = new ItemRequest(1L, "itemRequestDescription",
            user, LocalDateTime.MIN);
    private final Item itemNew = new Item(null, "item", "itemDescription", true,
            user, itemRequest);
    private final Item item = new Item(1L, "TestItem1", "itemDescription", true,
            user, itemRequest);

    @Test
    void searchItemsByTextInNameAndDescription_WithLowAndUpperCase() {
        manager.persist(userNew);
        manager.persist(itemRequestNew);
        manager.persist(itemNew);
        List<Item> items = repository.searchItemsByTextInNameAndDescription("ITem", PageRequest.of(0, 10));
        Assertions.assertNotNull(items);
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item, items.get(0));
    }
}