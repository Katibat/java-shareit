package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequest save(ItemRequest itemRequest, Long userId);

    ItemRequest findById(Long userId, Long itemRequestId);

    ItemRequestDto getItemReguestById(Long userId, Long itemRequestId);

    List<ItemRequestDto> getAllByUserId(Long userId);

    List<ItemRequestDto> findAll(int fromPage, int size, Long userId);

    void deleteById(Long itemRequestId);
}