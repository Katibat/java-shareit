package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService service;
    @SpyBean
    private BookingMapper bookingMapper;
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private User user1;
    private User user2;
    private Item item1;
    private Booking bookingNew;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingDtoNew bookingDtoNew;

    @BeforeEach
    void beforeEach(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
        user1 = User.builder()
                .id(1L)
                .name("name")
                .email("name@yandex.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("name2")
                .email("name2@yandex.ru")
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("itemDescription")
                .available(true)
                .owner(user1)
                .requestId(null)
                .build();
        bookingNew = Booking.builder()
                .id(null)
                .start(LocalDateTime.of(2022, 12, 1, 9, 00,00))
                .end(LocalDateTime.of(2022, 12, 10, 22, 00,00))
                .item(null)
                .booker(null)
                .status(null)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 12, 1, 9, 00,00))
                .end(LocalDateTime.of(2022, 12, 10, 22, 00,00))
                .item(item1)
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 12, 1, 9, 00,00))
                .end(LocalDateTime.of(2022, 12, 10, 22, 00,00))
                .item(item1)
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build();
        bookingDtoNew = BookingDtoNew.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2022, 12, 1, 9, 00,00))
                .end(LocalDateTime.of(2022, 12, 10, 22, 00,00))
                .build();
    }

    @Test
    void createBookingTest() throws Exception {
        Mockito.when(service.save(Mockito.any(Booking.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(booking);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingDtoNew))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$.booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void deleteByIdTest() throws Exception {
        mockMvc.perform(delete("/bookings/1")).andExpect(status().isOk());
    }

    @Test
    void changeBookingStatusTest() throws Exception {
        Mockito.when(service.changeBookingStatus(1L, 1L, true)).thenReturn(booking);
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").value(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.item").value(booking.getItem()))
                .andExpect(jsonPath("$.booker").value(booking.getBooker()))
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()));
    }

    @Test
    void changeBookingStatusIncorrectApproved() throws Exception {
        mockMvc.perform(patch("/bookings/1?approved=QWERT")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getBookingByIdTest() throws Exception {
        Mockito.when(service.findById(1L, 1L)).thenReturn(booking);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").value(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.item").value(booking.getItem()))
                .andExpect(jsonPath("$.booker").value(booking.getBooker()))
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()));
    }

    @Test
    void getBookingByIdWithIncorrectId() throws Exception {
        Mockito.when(service.findById(1L, 1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByBookerTest() throws Exception {
        Mockito.when(service.findAllByBooker(1, 1, 1L, "WAITING"))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings?state=WAITING&from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getAllByBookerDefaultState() throws Exception {
        Mockito.when(service.findAllByBooker(1, 1, 1L, "ALL"))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings?from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getAllByBookerDefaultStateAndPagination() throws Exception {
        Mockito.when(service.findAllByBooker(0, 10, 1L, "ALL"))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void findAllByOwnerTest() throws Exception {
        Mockito.when(service.findAllByOwner(1, 1, 1L, "WAITING"))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings/owner?state=WAITING&from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void findAllByOwnerDefaultState() throws Exception {
        Mockito.when(service.findAllByOwner(1, 1, 1L, "ALL"))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings/owner?from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void findAllByOwnerDefaultPagination() throws Exception {
        Mockito.when(service.findAllByOwner(0, 10, 1L, "ALL"))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }
}