package ru.practicum.shareit.request;

public class ItemRequestsMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                new ItemRequestDto.User(itemRequest.getRequestor().getId(),
                        itemRequest.getRequestor().getName()),
                itemRequest.getCreated()
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                null,
                itemRequestDto.getCreated()
        );
    }
}
