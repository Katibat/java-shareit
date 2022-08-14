package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class ItemServiceImpl implements ItemService {
    private final ItemStorage storage;

    @Autowired
    public ItemServiceImpl(ItemStorage storage) {
        this.storage = storage;
    }

    @Override
    public Item create(Item item, Long userId) {
        return storage.create(item, userId);
    }

    @Override
    public Item update(Long itemId, Long userId, Item item) {
        return storage.update(item, userId, itemId);
    }

    @Override
    public Item getById(Long userId, Long itemId) {
        return storage.getById(userId, itemId);
    }

    @Override
    public List<Item> getAllItemByOwner(Long userId) {
        return storage.getAllItemByOwner(userId);
    }

    @Override
    public List<Item> searchItems(Long userId, String text) {
        return storage.searchItemsByText(userId, text);
    }
}
