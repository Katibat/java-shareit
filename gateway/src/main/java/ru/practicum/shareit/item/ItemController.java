package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item={}, userId={}", itemDto, userId);
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable long itemId) {
        log.info("Updating items={}, userId={}", itemDto, userId);
        return itemClient.updateItem(itemId, userId, itemDto);

    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        log.info("Get item={}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItemById(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
                                                 @PathVariable @Positive long itemId) {
        log.info("Delete item={}, userId={}", itemId, ownerId);
        return itemClient.deleteItem(ownerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                   Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10")
                                                       Integer size) {
        log.info("Get owner items, userId={}", userId);
        return itemClient.getAllItems(from, size, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10")
                                                  Integer size,
                                              @RequestParam String text) {
        log.info("Get search items by text, text={}", text);
        return itemClient.searchItems(from, size, userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto text) {
        log.info("Creating comment={} about items={}, userId={}", text, itemId, userId);
        return itemClient.addComment(userId, itemId, text);
    }
}