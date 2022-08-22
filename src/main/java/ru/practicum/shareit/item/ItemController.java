package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;
    private final BookingService bookingService;

    @PostMapping // добавить новую вещь
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        Item item = ItemMapper.toItemNew(itemDto);
        return ItemMapper.toItemDto(service.save(item, userId));
    }

    @PatchMapping("/{itemId}") // редактировать вещь по идентификатору
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(service.update(userId, item));
    }

    @GetMapping("/{itemId}") // искать конкретную вещь по идентификатору
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable("itemId") Long itemId) {
        return service.getItemById(itemId, userId);
    }

    @GetMapping // получить список всех вещей владельца / пользователя
    public List<ItemDto> getAllItemByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllByOwnerIdFull(userId);
    }

    @GetMapping("/search") // найти вещи по тексту для потенциальных арендаторов
    public List<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        return service.searchItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment") // добавить новый отзыв об использовании вещи
    public CommentDto saveComment(@Valid @RequestBody CommentDto commentDto,
                                  @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId) {
        Comment comment = CommentMapper.toComment(commentDto);
        return CommentMapper.toCommentDto(service.saveComment(userId, itemId, comment));
    }
}