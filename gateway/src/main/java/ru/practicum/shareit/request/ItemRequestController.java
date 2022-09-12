package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                             @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Creating request {}, userId={}", requestDto, userId);
        return requestClient.addRequest(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                 @PathVariable long requestId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return requestClient.getRequest(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnerRequests(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Get all owner's requests, userId={}", userId);
        return requestClient.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                      Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10")
                                                          Integer size) {
        log.info("Get all requests, userId={}", userId);
        return requestClient.getAllRequests(userId, from, size);
    }
}