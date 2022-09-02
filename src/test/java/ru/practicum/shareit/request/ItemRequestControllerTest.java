package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private ItemService itemService;
    @SpyBean
    private ItemRequestsMapper itemRequestMapper;
    @SpyBean
    private ItemMapper itemMapper;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private Item item;
    private User user;
    private ItemDto itemDto;
    private ItemRequest itemRequest;
    private ItemRequest itemRequestNew;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
        item = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("itemDescription")
                .available(true)
                .owner(new User())
                .requestId(1L)
                .build();
        user = User.builder()
                .id(1L)
                .name("name")
                .email("name@yandex.ru")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("ItemDtoName")
                .description("itemDtoDescription")
                .available(true)
                .owner(new ItemDto.UserDto(1L, "userDto"))
                .requestId(1L)
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("itemRequestDescription")
                .requester(user)
                .created(null)
                .build();
        itemRequestNew = ItemRequest.builder()
                .id(null)
                .description("itemRequestDescription")
                .requester(null)
                .created(null)
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("itemRequestDescription")
                .requesterId(user.getId())
                .created(null)
                .items(List.of(itemDto))
                .build();
    }

    @Test
    void saveItemRequestTest() throws Exception {
        Mockito.when(itemRequestService.save(Mockito.any(ItemRequest.class), Mockito.anyLong()))
                .thenReturn(itemRequest);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestNew))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requesterId").value(itemRequestDto.getRequesterId()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated()));
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        Mockito.when(itemRequestService.getItemReguestById(1L, 1L)).thenReturn(itemRequestDto);
        Mockito.when(itemService.findItemsByRequestId(Mockito.anyLong())).thenReturn(List.of(item));
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requesterId").value(itemRequestDto.getRequesterId()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated()));
    }

    @Test
    void getItemRequestByIdWithoutRequest() throws Exception {
        Mockito.when(itemRequestService.getItemReguestById(1L, 1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemRequestsByUserTest() throws Exception {
        Mockito.when(itemRequestService.getAllByUserId(1L)).thenReturn(List.of(itemRequestDto));
        Mockito.when(itemService.findItemsByRequestId(Mockito.anyLong())).thenReturn(List.of(item));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].requesterId").value(itemRequestDto.getRequesterId()))
                .andExpect(jsonPath("$[0].created").value(itemRequestDto.getCreated()));
    }

    @Test
    void getAllTest() throws Exception {
        Mockito.when(itemRequestService.findAll(1, 1, 1L)).thenReturn(List.of(itemRequestDto));
        Mockito.when(itemService.findItemsByRequestId(1L)).thenReturn(List.of(item));
        mockMvc.perform(get("/requests/all?from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].requesterId").value(itemRequestDto.getRequesterId()))
                .andExpect(jsonPath("$[0].created").value(itemRequestDto.getCreated()));
    }

    @Test
    void getAllDefaultPagination() throws Exception {
        Mockito.when(itemRequestService.findAll(0, 10, 1L)).thenReturn(List.of(itemRequestDto));
        Mockito.when(itemService.findItemsByRequestId(1L)).thenReturn(List.of(item));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].requesterId").value(itemRequestDto.getRequesterId()))
                .andExpect(jsonPath("$[0].created").value(itemRequestDto.getCreated()));
    }

    @Test
    void deleteByIdTest() throws Exception {
        mockMvc.perform(delete("/requests/1")).andExpect(status().isOk());
    }
}