package ru.practicum.shareit.item;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item, Long userId);

    Item update(Item item, Long userId, Long itemId);

    Item getById(Long userId, Long itemId);

    List<Item> getAllItemByOwner(Long userId);

    List<Item> searchItemsByText(Long userId, String text);
}