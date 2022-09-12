package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequest save(ItemRequest itemRequest, Long userId) {
        itemRequest.setRequester(userService.findById(userId));
        return repository.save(itemRequest);
    }

    @Override
    public ItemRequest findById(Long itemRequestId) {
        return repository.findById(itemRequestId)
                .orElseThrow(() ->
                        new NotFoundException("Не найдена запрос вещи с идентификатором № " + itemRequestId));
    }

    @Override
    public ItemRequestDto getItemReguestById(Long userId, Long itemRequestId) {
        userService.checkIsUserExists(userId);
        ItemRequest itemRequest = findById(itemRequestId);
        ItemRequestDto itemRequestDto = ItemRequestsMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemRepository.findByRequestId(itemRequestId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList()));
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        userService.checkIsUserExists(userId);
        Sort sortBy = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequestDto> requests = repository.findByRequesterId(userId, sortBy)
                .stream().map(ItemRequestsMapper::toItemRequestDto).collect(Collectors.toList());
        requests.forEach(request -> request.setItems(itemRepository.findByRequestId(request.getId())
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList())));
        return requests;
    }

    @Override
    public List<ItemRequestDto> findAll(int fromPage, int size, Long userId) {
        userService.checkIsUserExists(userId);
        List<ItemRequestDto> requests = repository.findByRequesterIdNot(userId,
                PageRequest.of(fromPage, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream().map(ItemRequestsMapper::toItemRequestDto).collect(Collectors.toList());
        requests.forEach(request -> request.setItems(itemRepository.findByRequestId(request.getId())
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList())));
        return requests;
    }

    @Override
    public void deleteById(Long itemRequestId) {
        repository.deleteById(itemRequestId);
    }
}