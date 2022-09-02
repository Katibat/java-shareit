package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping // добавить новый запрос вещи
    public ItemRequestDto create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                          @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        ItemRequest itemRequest = ItemRequestsMapper.toItemRequest(itemRequestDto);
        return ItemRequestsMapper.toItemRequestDto(service.save(itemRequest, userId));
    }

    @GetMapping("/{requestId}") // искать конкретный запрос вещи по идентификатору
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable("requestId") Long itemRequestId) {
        return service.getItemReguestById(userId, itemRequestId);
    }

    @GetMapping // получить список запросов пользователя вместе с ответами на них
    public List<ItemRequestDto> getAllItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllByUserId(userId);
    }

    @GetMapping("/all") // получить список запросов, созданных другими пользователями
    public List<ItemRequestDto> getAll(@RequestParam(defaultValue = "0", value = "from", required = false)
                                           @PositiveOrZero int fromPage,
                                       @RequestParam(defaultValue = "10", required = false)
                                       @Positive int size,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.findAll(fromPage, size, userId);
    }

    @DeleteMapping("/{requestId}") // удалить запрос вещи
    public void deleteById(@PathVariable("requestId") Long requestId) {
        service.deleteById(requestId);
    }
}