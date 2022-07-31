package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping // добавить новую вещь
    public ItemDto create(@Validated(Create.class) @RequestBody ItemDto itemDto,
                        @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(service.create(item, userId));
    }

    @PatchMapping("/{itemId}") // редактировать вещь по идентификатору
    public ItemDto update(@PathVariable Long itemId,
                       @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @Validated(Update.class) @RequestBody ItemDto itemDto) {
        Item newItem = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(service.update(itemId, userId, newItem));
    }

    @GetMapping("/{itemId}") // искать конкретную вещь по идентификатору
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable("itemId") Long itemId) {
        return ItemMapper.toItemDto(service.getById(userId, itemId));
    }

    @GetMapping // получить список всех вещей владельца / пользователя
    public List<ItemDto> getAllItemByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllItemByOwner(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search") // найти вещи по тексту для потенциальных арендаторов
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(name = "text") String text) {
        return service.searchItems(userId, text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
