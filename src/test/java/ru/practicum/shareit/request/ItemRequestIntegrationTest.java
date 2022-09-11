package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {
    private final ItemRequestService service;
    private final UserService userService;

    @Test
    public void getAllByUserId() {
        User user = userService.save(new User(1L, "nameForTestIntegr", "nameForTestIntegr@yandex.ru"));
        System.out.println(user);
        ItemRequest itemRequest = service.save(new ItemRequest(
                2L, "itemRequestIntegDescription", null, LocalDateTime.now()), user.getId());
        ItemRequest itemRequest2 = service.save(new ItemRequest(
                3L, "itemRequestInteg2Description", null, LocalDateTime.now()), user.getId());
        List<ItemRequest> itemRequestList = service.getAllByUserId(user.getId())
                .stream()
                .map(ItemRequestsMapper::toItemRequest)
                .collect(Collectors.toList());
        Assertions.assertEquals(2, itemRequestList.size());
        Assertions.assertEquals(itemRequest2.getDescription(), itemRequestList.get(0).getDescription());
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestList.get(1).getDescription());
    }
}