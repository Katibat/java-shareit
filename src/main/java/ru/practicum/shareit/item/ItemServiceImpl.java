package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Item save(Item item, Long userId) {
        item.setOwner(userService.findById(userId));
        log.info("Создана вещь № {} пользователем № {}.", item.getId(), userId);
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
        log.info("Обновлена информация о вещи № {}.", item.getId());
        return repository.save(itemUpdate);
    }

    @Override
    @Transactional
    public void deleteById(Long itemId) {
        repository.deleteById(itemId);
        log.info("Удалена вещь № {}.", itemId);
    }

    @Override
    public Item findById(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundException("Не найдена вещь с идентификатором № " + itemId));
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = findById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        getComments(itemDto);
        if (itemDto.getOwner().getId().equals(userId)) {
            getLastAndNextBooking(itemDto);
            log.info("Передана информация о вещи {} ее собственнику {}.", item, userId);
        }
        log.debug("Передана вещь {}.", itemDto);
        return itemDto;
    }

    public List<ItemDto> getAllByOwnerId(int fromPage, int size, Long ownerId) {
        userService.checkIsUserExists(ownerId);
        Pageable pageable = PageRequest.of(fromPage / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<ItemDto> itemList = repository.findAllByOwnerId(ownerId, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemList.forEach(this::getLastAndNextBooking);
        itemList.forEach(this::getComments);
        log.info("Передан список вещей {} их собственнику {}.", itemList, ownerId);
        return itemList;
    }

    @Override
    public List<ItemDto> searchItems(int fromPage, int size, String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(fromPage / size, size);
        return repository.searchItemsByTextInNameAndDescription(text, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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
        log.info("Сохранен комментарий {} о бронировании вещи № {}.", comment, itemId);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getAllCommentsByItem(Long itemId) {
        log.info("Получен список комментариев о бронировании вещи № {}.", itemId);
        return commentRepository.findAllCommentsByItemId(itemId);
    }

    @Transactional
    void getComments(ItemDto itemDto) {
        itemDto.setComments(getAllCommentsByItem(itemDto.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        log.info("Сохранен список комментариев: {} о бронировании вещи № {}.",
                itemDto.getComments(), itemDto.getId());
    }

    @Transactional
    void getLastAndNextBooking(ItemDto itemDto) {
        if (!bookingRepository.findLastBooking(itemDto.getId(), LocalDateTime.now()).isEmpty()) {
            itemDto.setLastBooking(bookingRepository.findLastBooking(itemDto.getId(), LocalDateTime.now())
                    .stream().findFirst());
            log.info("Получено и сохранено время последнего бронирования для вещи № {} время: {}.",
                    itemDto.getId(), itemDto.getLastBooking());
        }
        if (!bookingRepository.findNextBooking(itemDto.getId(), LocalDateTime.now()).isEmpty()) {
            itemDto.setNextBooking(bookingRepository.findNextBooking(itemDto.getId(), LocalDateTime.now())
                    .stream().findFirst());
            log.info("Получено и сохранено время следующего бронирования для вещи № {} время: {}.",
                    itemDto.getId(), itemDto.getLastBooking());
        }
    }

    @Override
    public List<Item> findItemsByRequestId(Long requestId) {
        return repository.findByRequestId(requestId);
    }
}