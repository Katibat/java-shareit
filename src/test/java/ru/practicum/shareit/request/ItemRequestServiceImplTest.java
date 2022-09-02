package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private ItemRequestService service;
    @Mock
    private ItemRequestRepository repository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("name@yandex.ru")
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("description")
            .requester(user)
            .build();
    private final ItemRequest itemRequestNew = ItemRequest.builder()
            .id(null)
            .description("description")
            .requester(null)
            .build();

    @BeforeEach
    void beforeEach() {
        service = new ItemRequestServiceImpl(repository, itemRepository, userService);
    }

    @Test
    void saveRequestTest() {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(repository.save(itemRequestNew)).thenReturn(itemRequest);
        ItemRequest result = service.save(itemRequestNew, 1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(itemRequest, result);
    }

    @Test
    void findRequestByIdTest() {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(null);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(itemRequest));
        ItemRequest result = service.findById(1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(itemRequest, result);
    }

    @Test
    void getAllByUserIdTest() {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(null);
        Mockito.when(repository.findByRequesterId(1L, Sort.by(Sort.Direction.DESC, "created")))
                .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> itemRequestList = service.getAllByUserId(1L);
        ItemRequestDto expected = ItemRequestsMapper.toItemRequestDto(itemRequest);
        Assertions.assertNotNull(itemRequestList);
        Assertions.assertEquals(expected, itemRequestList.get(0));
    }

    @Test
    void getAllByUserIdWithoutRequests() {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(null);
        Mockito.when(repository.findByRequesterId(1L, Sort.by(Sort.Direction.DESC, "created")))
                .thenReturn(Collections.emptyList());
        List<ItemRequestDto> itemRequestList = service.getAllByUserId(1L);
        ItemRequestDto expected = ItemRequestsMapper.toItemRequestDto(itemRequest);
        Assertions.assertNotNull(itemRequestList);
        Assertions.assertEquals(0, itemRequestList.size());
    }

    @Test
    void findAllTest() {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(null);
        Mockito.when(repository.findByRequesterIdNot(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> itemRequestList = service.findAll(0, 10, 1L);
        ItemRequestDto expected = ItemRequestsMapper.toItemRequestDto(itemRequest);
        Assertions.assertNotNull(itemRequestList);
        Assertions.assertEquals(expected, itemRequestList.get(0));
    }

    @Test
    void findAllWithoutRequests() {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(null);
        Mockito.when(repository.findByRequesterIdNot(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(Collections.emptyList());
        List<ItemRequestDto> itemRequestList = service.findAll(0, 10, 1L);
        Assertions.assertNotNull(itemRequestList);
        Assertions.assertEquals(0, itemRequestList.size());
    }

    @Test
    void deleteRequestTest() {
        service.deleteById(1L);
        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deleteUserIncorrectIdTest() {
        Mockito.doThrow(NotFoundException.class).when(repository).deleteById(Mockito.anyLong());
        Assertions.assertThrows(NotFoundException.class, () -> service.deleteById(1L));
        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }
}