package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemIntegrationTest {

    private final ItemService service;
    private final UserService userService;

    @Test
    public void findAllByOwner() {
        User user = userService.save(new User(1L, "name", "name@yandex.ru"));
        Item item1 = service.save(new Item(1L, "ItemName1", "item1Description",
                true, null, null), user.getId());
        Item item2 = service.save(new Item(2L, "ItemName2", "item2Description",
                true, null, null), user.getId());
        Item item3 = service.save(new Item(3L, "ItemName3", "item3Description",
                true, null, null), user.getId());

        List<Item> itemList = service.getAllByOwnerId(1, 10, 1L)
                .stream()
                .map(ItemMapper::toItem)
                .collect(Collectors.toList());
        Assertions.assertEquals(3, itemList.size());
        Assertions.assertEquals(item1.getName(), itemList.get(0).getName());
        Assertions.assertEquals(item2.getName(), itemList.get(1).getName());
        Assertions.assertEquals(item3.getName(), itemList.get(2).getName());
    }
}