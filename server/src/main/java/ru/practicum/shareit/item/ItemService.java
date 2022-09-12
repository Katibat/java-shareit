package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.Comment;

import java.util.List;

public interface ItemService {

    Item save(Long userId, Item item);

    Item update(Long userId, Item item);

    void deleteById(Long itemId);

    Item findById(Long itemId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getAllByOwnerId(int fromPage, int size, Long userId);

    List<ItemDto> searchItems(int fromPage, int size, String text);

    Comment saveComment(Long userId, Long itemId, Comment comment);

    List<Comment> getAllCommentsByItem(Long itemId);

    List<Item> findItemsByRequestId(Long requestId);
}
