package ru.practicum.shareit.request;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {

    ItemRequest save(ItemRequest itemRequest);

    ItemRequest update(Long itemRequestId, ItemRequest itemRequest);

    void delete(Long itemRequestId);

    Optional<ItemRequest> findById(Long itemRequestId);

    List<ItemRequest> findAll();
}
