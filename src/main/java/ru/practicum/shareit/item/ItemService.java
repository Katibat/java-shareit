package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.Comment;

import java.util.List;

public interface ItemService {
    Item save(Item item, Long userId);

    Item update(Long userId, Item item);

    void deleteById(Long itemId);

    Item findById(Long itemId);

    List<Item> getAllByOwnerId(Long userId);

    List<Item> searchItems(String text);

    Comment saveComment(Long userId, Long itemId, Comment comment);

    List<Comment> getAllCommentsByItem(Long itemId);
}
