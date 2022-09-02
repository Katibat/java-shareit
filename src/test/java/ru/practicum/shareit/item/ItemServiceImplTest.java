package ru.practicum.shareit.item;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private ItemService service;
    @Mock
    private ItemRepository repository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentRepository commentRepository;
    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("name@yandex.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("name2")
            .email("name2@yandex.ru")
            .build();
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("name")
            .email("name@yandex.ru")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("ItemName")
            .description("itemDescription")
            .available(true)
            .owner(user)
            .requestId(null)
            .build();
    private final Item itemNew = Item.builder()
            .id(null)
            .name("ItemName")
            .description("itemDescription")
            .available(true)
            .owner(null)
            .requestId(null)
            .build();
    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("comment")
            .authorName("user2")
            .created(LocalDateTime.MIN)
            .build();
    private final Comment comment = Comment.builder()
            .id(1L)
            .text("comment")
            .author(user2)
            .created(LocalDateTime.MIN)
            .build();
    private final Comment commentNew = Comment.builder()
            .id(null)
            .text("comment")
            .author(user2)
            .created(LocalDateTime.MIN)
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("ItemName")
            .description("itemDescription")
            .available(true)
            .owner(null)
            .requestId(null)
            .lastBooking(null)
            .nextBooking(null)
            .comments(List.of(commentDto))
            .build();

    @BeforeEach
    void beforeEach() {
        service = new ItemServiceImpl(repository, commentRepository, userService, bookingRepository);
    }

    @Test
    void saveItemTest() {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(repository.save(Mockito.any(Item.class))).thenReturn(item);
        Item result = service.save(itemNew, user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(item, result);
    }

    @Test
    void updateItemTest() {
        Item toUpdate = new Item(1L, "nameUpdate", "descriptionUpdate",
                true, user, null);
        Item updated = new Item(1L, "nameUpdate", "descriptionUpdate",
                true, user, null);
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(repository.save(updated)).thenReturn(updated);
        Item result = service.update(1L, toUpdate);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(updated, result);
    }

    @Test
    void updateItemWithIncorrectOwnerIdTest() {
        Item toUpdateItem = new Item(1L, "name", "description",
                true, null, null);
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Assertions.assertThrows(NotFoundException.class, () -> service.update(2L, toUpdateItem));
    }

    @Test
    void updateItemWithIncorrectIdTest() {
        Item toUpdateItem = new Item(1L, "nameUpdate", "descriptionUpdate",
                true, null, null);
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> service.update(1L, toUpdateItem));
    }

    @Test
    void deleteItemByIdTest() {
        service.deleteById(1L);
        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void findItemByIdTest() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(item));
        Item result = service.findById(1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(item, result);
    }

    @Test
    void findItemByIncorrectIdTest() {
        Mockito.when(repository.findById(18L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(18L));
    }

    @Test
    void getItemByIdTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllCommentsByItemId(Mockito.anyLong())).thenReturn(List.of(comment));
        Item result = ItemMapper.toItem(service.getItemById(1L, 1L));
        result.setOwner(user);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(item, result);
    }

    @Test
    void getItemByIdTestWithIncorrectId() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> service.getItemById(1L, 1L));
    }

    @Test
    void getAllItemsByOwnerIdTest() {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(repository.findAllByOwnerId(1L,
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"))))
                .thenReturn(List.of(item));
        Mockito.when(bookingRepository.findLastBooking(1L, LocalDateTime.now())).thenReturn(null);
        Mockito.when(bookingRepository.findNextBooking(1L, LocalDateTime.now())).thenReturn(null);
        Mockito.when(commentRepository.findAllCommentsByItemId(1L)).thenReturn((List.of(comment)));
        List<Item> itemList = service.getAllByOwnerId(0, 10, 1L)
                .stream()
                .map(ItemMapper::toItem)
                .collect(Collectors.toList());
        itemList.get(0).setOwner(user);
        Assertions.assertNotNull(itemList);
        Assertions.assertEquals(1, itemList.size());
        Assertions.assertEquals(item, itemList.get(0));
    }

    @Test
    void getAllItemsByOwnerIdTestWithoutItems() {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(repository.findAllByOwnerId(1L,
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"))))
                .thenReturn(Collections.emptyList());
        List<Item> itemList = service.getAllByOwnerId(0, 10, 1L)
                .stream().map(ItemMapper::toItem).collect(Collectors.toList());
        Assertions.assertNotNull(itemList);
        Assertions.assertEquals(0, itemList.size());
    }

    @Test
    void searchItemsTest() {
        Item searchItem = new Item(1L, "ItemName", "itemDescription",
                true, null, null);
        Mockito.when(repository.searchItemsByTextInNameAndDescription("text", PageRequest.of(0, 10)))
                .thenReturn((List.of(item)));
        List<Item> itemList = service.searchItems(0, 10, "text")
                .stream().map(ItemMapper::toItem).collect(Collectors.toList());
        Assertions.assertNotNull(itemList);
        Assertions.assertEquals(1, itemList.size());
        Assertions.assertEquals(searchItem, itemList.get(0));
    }

    @Test
    void searchItemsWithBlankTextTest() {
        List<Item> itemList = service.searchItems(0, 10, " ")
                .stream().map(ItemMapper::toItem).collect(Collectors.toList());
        Assertions.assertNotNull(itemList);
        Assertions.assertEquals(0, itemList.size());
    }

    @Test
    void saveCommentTest() {
        Booking booking = new Booking();
        Mockito.when(bookingRepository.findCompletedBooking(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(LocalDateTime.class))).thenReturn(booking);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(userService.findById(1L)).thenReturn(user);
        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);
        Comment result = service.saveComment(1L, 1L, commentNew);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(comment, result);
    }

    @Test
    void createCommentWithoutBookingTest() {
        Mockito.when(bookingRepository.findCompletedBooking(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(LocalDateTime.class))).thenReturn(null);
        Assertions.assertThrows(ValidationException.class, () -> service.saveComment(1L, 1L, commentNew));
    }
}