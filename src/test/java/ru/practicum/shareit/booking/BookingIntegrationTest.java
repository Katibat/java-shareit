package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingIntegrationTest {
    private final BookingService service;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    public void findAllByOwner() {
        User user1 = userService.save(new User(1L, "name", "name@yandex.ru"));
        User user2 = userService.save(new User(2L, "name2", "name2@yandex.ru"));
        Item item = itemService.save(new Item(1L, "ItemName", "itemDescription",
                true, null, null), user2.getId());
        Booking booking = service.save(new Booking(1L,
                LocalDateTime.of(2022, 12, 1, 9, 00,00),
                LocalDateTime.of(2022, 12, 10, 22, 00,00),
                item, null, BookingStatus.APPROVED), user1.getId(), item.getId());
        List<Booking> bookingList = service.findAllByOwner(1, 10, 2L, "ALL")
                .stream()
                .map(BookingMapper::toBooking)
                .collect(Collectors.toList());
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(booking.getStart(), bookingList.get(0).getStart());
    }
}