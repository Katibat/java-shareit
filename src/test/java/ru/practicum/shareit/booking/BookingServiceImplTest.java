package ru.practicum.shareit.booking;

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
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private BookingService service;
    @Mock
    private BookingRepository repository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    private final User user1 = User.builder()
            .id(1L)
            .name("name")
            .email("name@yandex.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("name2")
            .email("name2@yandex.ru")
            .build();
    private final Item item1 = Item.builder()
            .id(1L)
            .name("ItemName")
            .description("itemDescription")
            .available(true)
            .owner(user2)
            .requestId(null)
            .build();
    private final Item item2 = Item.builder()
            .id(2L)
            .name("ItemName2")
            .description("item2Description")
            .available(false)
            .owner(user2)
            .requestId(null)
            .build();
    private final Booking bookingNew = Booking.builder()
            .id(null)
            .start(LocalDateTime.of(2022, 12, 1, 9, 00,00))
            .end(LocalDateTime.of(2022, 12, 10, 22, 00,00))
            .item(null)
            .booker(null)
            .status(null)
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.of(2022, 12, 1, 9, 00,00))
            .end(LocalDateTime.of(2022, 12, 10, 22, 00,00))
            .item(item1)
            .booker(user1)
            .status(BookingStatus.WAITING)
            .build();

    @BeforeEach
    void beforeEach() {
        service = new BookingServiceImpl(repository, itemService, userService);
    }

    @Test
    void saveBookingTest() {
        Mockito.when(itemService.findById(1L)).thenReturn(item1);
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Mockito.when(repository.save(Mockito.any(Booking.class))).thenReturn(booking);
        Booking result = service.save(bookingNew, 1L, 1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking, result);
    }

    @Test
    void saveBookingForItemsOwner() {
        Mockito.when(itemService.findById(1L)).thenReturn(item1);
        Mockito.when(userService.findById(1L)).thenReturn(user2);
        Assertions.assertThrows(NullPointerException.class,
                () -> service.save(bookingNew, 2L, 1L));
    }

    @Test
    void saveBookingWithNotAvailableItem() {
        Mockito.when(itemService.findById(2L)).thenReturn(item2);
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Assertions.assertThrows(ValidationException.class,
                () -> service.save(bookingNew, 1L, 2L));
    }

    @Test
    void saveBookingWithEndBeforeStart() {
        final Booking bookingFailTime = Booking.builder()
                .id(null)
                .start(LocalDateTime.of(2022, 12, 10, 9, 00,00))
                .end(LocalDateTime.of(2022, 12, 1, 22, 00,00))
                .item(null)
                .booker(null)
                .status(null)
                .build();
        Mockito.when(itemService.findById(1L)).thenReturn(item1);
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Assertions.assertThrows(ValidationException.class, () -> service.save(bookingFailTime, 1L, 1L));
    }

    @Test
    void deleteBookingTest() {
        service.deleteById(1L);
        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deleteBookingIncorrectIdTest() {
        Mockito.doThrow(NotFoundException.class).when(repository).deleteById(Mockito.anyLong());
        Assertions.assertThrows(NotFoundException.class, () -> service.deleteById(4L));
        Mockito.verify(repository, Mockito.times(1)).deleteById(4L);
    }

    @Test
    void changeBookingStatusAPPROVED() {
        final Booking bookingUpdate = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 12, 10, 9, 00,00))
                .end(LocalDateTime.of(2022, 12, 1, 22, 00,00))
                .item(item1)
                .booker(user1)
                .status(BookingStatus.APPROVED)
                .build();
        Mockito.when(itemService.findById(1L)).thenReturn(item1);
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(repository.save(Mockito.any(Booking.class))).thenReturn(bookingUpdate);
        Booking result = service.changeBookingStatus(2L, 1L, true);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookingUpdate, result);
    }

    @Test
    void changeBookingStatusREJECTED() {
        final Booking bookingUpdate = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 12, 10, 9, 00,00))
                .end(LocalDateTime.of(2022, 12, 1, 22, 00,00))
                .item(item1)
                .booker(user1)
                .status(BookingStatus.REJECTED)
                .build();
        Mockito.when(itemService.findById(1L)).thenReturn(item1);
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(repository.save(Mockito.any(Booking.class))).thenReturn(bookingUpdate);
        Booking result = service.changeBookingStatus(2L, 1L, false);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookingUpdate, result);
    }

    @Test
    void changeBookingStatusAlreadyAPPROVED() {
        final Booking bookingUpdate = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 12, 10, 9, 00,00))
                .end(LocalDateTime.of(2022, 12, 1, 22, 00,00))
                .item(item1)
                .booker(user1)
                .status(BookingStatus.APPROVED)
                .build();
        Mockito.when(itemService.findById(1L)).thenReturn(item1);
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(bookingUpdate));
        Assertions.assertThrows(StateException.class,
                () -> service.changeBookingStatus(2L, 1L, true));
    }

    @Test
    void changeBookingStatusAlreadyREJECTED() {
        final Booking bookingUpdate = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 12, 10, 9, 00,00))
                .end(LocalDateTime.of(2022, 12, 1, 22, 00,00))
                .item(item1)
                .booker(user1)
                .status(BookingStatus.REJECTED)
                .build();
        Mockito.when(itemService.findById(1L)).thenReturn(item1);
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(bookingUpdate));
        Assertions.assertThrows(StateException.class,
                () -> service.changeBookingStatus(2L, 1L, false));
    }

    @Test
    void changeBookingStatusByNotOwner() {
        Mockito.when(itemService.findById(1L)).thenReturn(item1);
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(booking));
        Assertions.assertThrows(NotFoundException.class,
                () -> service.changeBookingStatus(1L, 1L, false));
    }

    @Test
    void findBookingByIdTest() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(booking));
        Booking result = service.findById(1L, 1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking, result);
    }

    @Test
    void findBookingByIdWithIncorrectId() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(1L, 1L));
    }

    @Test
    void findAllByBookerStateALL() {
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Mockito.when(repository.findAllByBookerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByBooker(0, 10, 1L, "ALL")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByBookerStatePAST() {
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Mockito.when(repository.findPastByBookerId(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByBooker(0, 10, 1L, "PAST")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByBookerStateFUTURE() {
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Mockito.when(repository.findFutureByBookerId(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByBooker(0, 10, 1L, "FUTURE")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByBookerStateCURRENT() {
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Mockito.when(repository.findCurrentByBookerId(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByBooker(0, 10, 1L, "CURRENT")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByBookerStateWAITING() {
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Mockito.when(repository.findByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(BookingStatus.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByBooker(0, 10, 1L, "WAITING")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByBookerStateREJECTED() {
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Mockito.when(repository.findByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(BookingStatus.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByBooker(0, 10, 1L, "REJECTED")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByBookerWithIncorrectState() {
        Mockito.when(userService.findById(1L)).thenReturn(user1);
        Assertions.assertThrows(StateException.class,
                () -> service.findAllByBooker(0, 10, 1L, "QWERT"));
    }

    @Test
    void findAllByOwnerStateALL() {
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(repository.findAllByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByOwner(0, 10, 2L, "ALL")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByOwnerStateStatePAST() {
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(repository.findPastByOwnerId(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByOwner(0, 10, 2L, "PAST")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByOwnerStateFUTURE() {
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(repository.findFutureByOwnerId(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByOwner(0, 10, 2L, "FUTURE")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByOwnerStateCURRENT() {
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(repository.findCurrentByOwnerId(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByOwner(0, 10, 2L, "CURRENT")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByOwnerStateWAITING() {
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(repository.findByOwnerIdAndStatus(Mockito.anyLong(), Mockito.any(BookingStatus.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByOwner(0, 10, 2L, "WAITING")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByOwnerStateREJECTED() {
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(repository.findByOwnerIdAndStatus(Mockito.anyLong(), Mockito.any(BookingStatus.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = service.findAllByOwner(0, 10, 2L, "REJECTED")
                .stream().map(BookingMapper::toBooking).collect(Collectors.toList());
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void findAllByOwnerStateIncorrect() {
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Assertions.assertThrows(StateException.class,
                () -> service.findAllByOwner(0, 10, 2L, "QWERT"));
    }
}