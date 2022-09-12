package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

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

    @DeleteMapping("/{itemId}") // удалить вещь
    public void deleteById(@PathVariable("itemId") Long itemId) {
        service.deleteById(itemId);
    }

    @GetMapping("/{itemId}") // искать конкретную вещь по идентификатору
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable("itemId") Long itemId) {
        return service.getItemById(itemId, userId);
    }

    @GetMapping // получить список всех вещей владельца / пользователя
    public List<ItemDto> getAllItemByOwner(@RequestParam(value = "from", defaultValue = "0", required = false)
                                               @PositiveOrZero int fromPage,
                                           @RequestParam(defaultValue = "10", required = false) @Positive int size,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllByOwnerId(fromPage, size, userId);
    }

    @GetMapping("/search") // найти вещи по тексту для потенциальных арендаторов
    public List<ItemDto> searchItems(@RequestParam(value = "from", defaultValue = "0", required = false)
                                         @PositiveOrZero int fromPage,
                                     @RequestParam(defaultValue = "10", required = false) @Positive int size,
                                     @RequestParam(name = "text") String text) {
        return service.searchItems(fromPage, size, text);
    }

    @PostMapping("/{itemId}/comment") // добавить новый отзыв об использовании вещи
    public CommentDto saveComment(@Valid @RequestBody CommentDto commentDto,
                                  @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId) {
        Comment comment = CommentMapper.toComment(commentDto);
        return CommentMapper.toCommentDto(service.saveComment(userId, itemId, comment));
    }
}