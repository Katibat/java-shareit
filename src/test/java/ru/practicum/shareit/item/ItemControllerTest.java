package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService service;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemRequestService itemRequestService;
    @SpyBean
    private ItemMapper itemMapper;
    @SpyBean
    private CommentMapper commentMapper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private User user;
    private User user2;
    private Item item;
    private Item itemNew;
    private Comment comment;
    private CommentDto commentDto;
    private Comment commentNew;
    private ItemDto itemDto;
    private ItemDto itemDtoNew;

    @BeforeEach
    void beforeEach(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
        user = User.builder()
                .id(1L)
                .name("name")
                .email("name@yandex.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("name2")
                .email("name2@yandex.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .requestId(null)
                .build();
        itemNew = Item.builder()
                .id(null)
                .name("ItemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .requestId(null)
                .build();
        comment = Comment.builder()
                .id(1L)
                .text("comment")
                .item(item)
                .author(user2)
                .created(LocalDateTime.MIN)
                .build();
        commentNew = Comment.builder()
                .id(null)
                .text("comment")
                .item(null)
                .author(null)
                .created(null)
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("user2")
                .created(LocalDateTime.MIN)
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("ItemName")
                .description("itemDescription")
                .available(true)
                .owner(new ItemDto.UserDto(1L, "name"))
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();
        itemDtoNew = ItemDto.builder()
                .id(null)
                .name("ItemName")
                .description("itemDescription")
                .available(true)
                .owner(new ItemDto.UserDto(1L, "name"))
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();
    }

    @Test
    void createItemTest() throws Exception {
        Mockito.when(service.save(Mockito.any(Item.class), Mockito.anyLong())).thenReturn(item);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoNew))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void updateItemTest() throws Exception {
        Mockito.when(service.update(Mockito.anyLong(), Mockito.any(Item.class)))
                .thenReturn(item);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemNew))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateItemTestWithIncorrectId() throws Exception {
        Mockito.when(service.update(Mockito.anyLong(), Mockito.any(Item.class)))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemNew))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteByIdTest() throws Exception {
        mockMvc.perform(delete("/items/1")).andExpect(status().isOk());
    }

    @Test
    void getItemByIdTest() throws Exception {
        Mockito.when(service.getItemById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemDto);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void getItemByIdWithIncorrectId() throws Exception {
        Mockito.when(service.getItemById(Mockito.anyLong(), Mockito.anyLong())).thenThrow(NotFoundException.class);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemsByOwnerIdTest() throws Exception {
        Mockito.when(service.getAllByOwnerId(1, 1, 1L)).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items?from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void getAllItemsByOwnerIdWithDefaultPagination() throws Exception {
        Mockito.when(service.getAllByOwnerId(0, 10, 1L)).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void searchItemsByTextInNameAndDescriptionTestOk() throws Exception {
        Mockito.when(service.searchItems(1, 1, "text")).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search?text=text&from=1&size=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void searchItemsByTextInNameAndDescriptionTestWithDefaultPagination() throws Exception {
        Mockito.when(service.searchItems(0, 10, "text")).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search?text=text")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void saveCommentTestO() throws Exception {
        Mockito.when(service.saveComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(Comment.class)))
                .thenReturn(comment);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentNew))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.text").value(comment.getText()))
                .andExpect(jsonPath("$.authorName").value(comment.getAuthor().getName()));
    }

    @Test
    void saveCommentTestValidationException() throws Exception {
        Mockito.when(service.saveComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(Comment.class)))
                .thenThrow(ValidationException.class);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentNew))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}