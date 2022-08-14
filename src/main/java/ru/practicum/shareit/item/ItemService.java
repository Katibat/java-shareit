package ru.practicum.shareit.item;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item, Long userId);

    Item update(Long itemId, Long userId, Item item);

    Item getById(Long userId, Long itemId);

    List<Item> getAllItemByOwner(Long userId);

    List<Item> searchItems(Long userId, String text);
}
