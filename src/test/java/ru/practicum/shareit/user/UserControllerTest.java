package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService service;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    private User user;
    private User userNew;

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
        userNew = User.builder()
                .id(null)
                .name("name")
                .email("name@yandex.ru")
                .build();
    }

        @Test
    void saveNewUser() throws Exception {
        Mockito.when(service.save(userNew)).thenReturn(user);
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userNew))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void updateUserTestOk() throws Exception {
        Mockito.when(service.update(1L, user)).thenReturn(user);
        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void updateWithIncorrectId() throws Exception {
        Mockito.when(service.update(1L, user)).thenThrow(NotFoundException.class);
        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
    }

    @Test
    void findUserByIdTest() throws Exception {
        Mockito.when(service.findById(1L)).thenReturn(user);
        mockMvc.perform(get("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void findUserByIncorrectIdTest() throws Exception {
        Mockito.when(service.findById(1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(get("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllUsersTest() throws Exception {
        Mockito.when(service.findAll()).thenReturn(List.of(user));
        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].name").value(user.getName()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()));
    }
}