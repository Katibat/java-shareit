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
    public ResponseEntity<Object> addItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item={}, userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto,
                                             @Positive @PathVariable long itemId) {
        log.info("Updating items={}, userId={}", itemDto, userId);
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Positive @PathVariable Long itemId) {
        log.info("Get item={}, userId={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItemById(@Positive @RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                                 @Positive @PathVariable Long itemId) {
        log.info("Delete item={}, userId={}", itemId, ownerId);
        return itemClient.deleteItem(ownerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnerItems(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get owner items, userId={}", userId);
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(name = "text", required = false) String text) {
        log.info("Get search items by text, text={}", text);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Positive @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Creating comment={} about items={}, userId={}", commentDto, itemId, userId);
        return itemClient.addComment(userId, commentDto, itemId);
    }
}