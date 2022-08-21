package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
//    private final BookingService bookingService;

    @Override
    @Transactional
    public Item save(Item item, Long userId) {
        item.setOwner(userService.findById(userId));
        return repository.save(item);
    }

    @Override
    @Transactional
    public Item update(Long userId, Item item) {
        Item itemUpdate = repository.findById(item.getId())
                .orElseThrow(() ->
                        new NotFoundException("Не найдена вещь с идентификатором № " + item.getId()));
        if (!Objects.equals(itemUpdate.getOwner().getId(), userId)) {
            throw new NotFoundException("Недостаточно прав для обновления информации " +
                    "о вещи с идентификатором № " + item.getId());
        }
        if (item.getName() != null) {
            itemUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemUpdate.setAvailable(item.getAvailable());
        }
        return repository.save(itemUpdate);
    }

    @Override
    @Transactional
    public void deleteById(Long itemId) {
        repository.deleteById(itemId);
    }

    @Override
    public Item findById(Long itemId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundException("Не найдена вещь с идентификатором № " + itemId));

        return item;
    }

    @Override
    public List<Item> getAllByOwnerId(Long userId) {
        List<Item> itemList = repository.findAllByOwnerId(userId);
        itemList.sort(Comparator.comparing(Item::getId));
        return itemList;
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return repository.searchItemsByTextInNameAndDescription(text);
    }

    @Override
    @Transactional
    public Comment saveComment(Long userId, Long itemId, Comment comment) {
        Booking booking = bookingRepository.findCompletedBooking(userId, itemId, LocalDateTime.now());
        if (booking == null) {
            throw new ValidationException("Не найдено бронирование вещи для пользователя " +
                    "с идентификатором № " + userId);
        }
        comment.setItem(repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с идентификатором № " + itemId)));
        comment.setAuthor(userService.findById(userId));
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getAllCommentsByItem(Long itemId) {
        return commentRepository.findAllCommentsByItemId(itemId);
    }

//    private void addComments(Item item) {
//        List<Comment> list = new ArrayList<>(getAllCommentsByItem(item.getId()));
//        item.setComments(list);
//    }


//    private void addComments(Item item) {
//        List<Comment> list = new ArrayList<>(getAllCommentsByItem(item.getId()));
//        item.setComments(list);
//    }
//
//    private void addLastAndNextBooking(Item item) {
//        if (bookingRepository.findLastBooking(item.getId(), LocalDateTime.now()) != null) {
//            item.setLastBooking((Booking) bookingRepository.findLastBooking(item.getId(), LocalDateTime.now()));
//        }
//        if (bookingRepository.findNextBooking(item.getId(), LocalDateTime.now()) != null) {
//            item.setNextBooking((Booking) bookingRepository.findNextBooking(item.getId(), LocalDateTime.now()));
//        }
//    }
}
