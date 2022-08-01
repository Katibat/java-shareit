package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private final UserService userService;
    private Long idCounter = 0L;

    @Autowired
    public ItemStorageImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Item create(Item item, Long userId) {
        if (userService.getById(userId) == null) {
            log.warn("При сохдании вещи не найден пользователь с идентификатором № {}.", userId);
            throw new NotFoundException("Не найден пользователь с идентификатором № " + userId);
        }
        item.setId(generateId());
        item.setOwner(userService.getById(userId));
        final List<Item> itemsForMap = items.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        itemsForMap.add(item);
        items.put(userId, itemsForMap);
        log.info("Пользователем {} добавлена вещь {}.", userId, item.getName());
        return item;
    }

    @Override
    public Item update(Item updateItem, Long userId, Long itemId) {
        Item item = getById(userId, itemId);
        if (!userId.equals(item.getOwner().getId())) {
            log.warn("Запрошено обновление информации о вещи с идентификатором № {} от пользователя {}, " +
                    "ей не владеющего", itemId, userId);
            throw new ForbiddenException("Пользователю не доступно редактирование вещи с идентификатором № " + itemId);
        }
        if (updateItem.getName() != null && !updateItem.getName().isBlank()) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null && !updateItem.getDescription().isBlank()) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }
        log.info("Обновлена информация для вещи с идентификатором № {}", item.getId());
        return item;
    }

    @Override
    public Item getById(Long userId, Long itemId) {
        if (userService.getById(userId) == null) {
            log.warn("При получении вещи по идентификатору не найден пользователь с идентификатором № {}.", userId);
            throw new NotFoundException("Не найден пользователь с идентификатором № " + userId);
        }
        Optional<Item> foundItem = getAllItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
        if (foundItem.isEmpty()) {
            log.warn("При получении вещи по идентификатору вещь с идентификатором № {} не найдена.", itemId);
            throw new NotFoundException("Не найдена вещь с идентификатором № " + itemId);
        }
        log.info("Получена вещь с идентификатором № {}.", itemId);
        return foundItem.get();
    }

    @Override
    public List<Item> getAllItemByOwner(Long userId) {
        if (userService.getById(userId) == null) {
            log.warn("При получении списка вещей не найден собственник с идентификатором № {}.", userId);
            throw new NotFoundException("Не найден пользователь с идентификатором № " + userId);
        }
        List<Item> itemList = new ArrayList<>(items.get(userId));
        log.info("Получен список вещей пользователя с идентификатором № {}.", userId);
        return itemList;
    }

    @Override
    public List<Item> searchItemsByText(Long userId, String text) {
        List<Item> itemList = new ArrayList<>();
        if (!text.isBlank()) {
            log.info("Получен список вещей, найденных по введенному тексту: {}.", text);
            itemList = getAllItems().stream()
                    .filter(item -> item.getAvailable().equals(Boolean.TRUE)
                            && (item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                    .collect(Collectors.toList());
        }
        return itemList;
    }

    private void deleteItemById(Long userId, Long itemId) {
        Item item = getById(userId, itemId);
        if (!userId.equals(item.getOwner().getId())) {
            log.warn("Запрошено удаление вещи с идентификатором № {} от пользователя {}, " +
                    "ей не владеющего", itemId, userId);
            throw new ValidationException("Пользователю не доступно удаление вещи с идентификатором № " + itemId);
        }
        List<Item> itemList = new ArrayList<>();
        items.get(userId).forEach(itemList::remove);
        items.put(userId, itemList);
        log.info("Из списка вещей пользователя № {} удалена вещь с идентификатором № {}.", userId, itemId);
    }

    private void deleteAllItemsByOwner(Long userId) {
        items.remove(userId);
    }

    private void deleteAllItems() {
        items.clear();
        idCounter = 0L;
    }

    private List<Item> getAllItems() {
        List<Item> allItemsList = new ArrayList<>();
        items.values()
                .forEach(c -> allItemsList.addAll(c));
        return allItemsList;
    }

    private Long generateId() {
        idCounter++;
        return idCounter;
    }
}