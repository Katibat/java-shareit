package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;

    @Override
    @Transactional
    public ItemRequest save(ItemRequest itemRequest) {
        return repository.save(itemRequest);
    }

    @Override
    @Transactional
    public ItemRequest update(Long itemRequestId, ItemRequest itemRequest) {
        ItemRequest itemRequestUpdate = repository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Не найден заказ с идентификатором № " + itemRequestId));
        return repository.save(itemRequestUpdate);
    }

    @Override
    @Transactional
    public void delete(Long itemRequestId) {
        repository.deleteById(itemRequestId);

    }

    @Override
    public Optional<ItemRequest> findById(Long itemRequestId) {
        return repository.findById(itemRequestId);
    }

    @Override
    public List<ItemRequest> findAll() {
        return repository.findAll();
    }
}
