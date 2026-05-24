package app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.dto.UserDto;
import app.dto.UserRequest;
import app.entity.User;
import app.exception.DuplicateEntityException;
import app.exception.EntityNotFoundException;
import app.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import app.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private UserMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_success() throws Exception {
        UserRequest req = new UserRequest();
        req.setName("test");
        req.setEmail("test@mail.com");
        req.setAge(30);

        User user = new User("test", "test@mail.com", 30);
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("test");
        dto.setEmail("test@mail.com");
        dto.setAge(30);
        dto.setCreatedAt(LocalDateTime.now());

        when(mapper.toEntity(any())).thenReturn(user);
        when(mapper.toDto(any())).thenReturn(dto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@mail.com"));
    }

    @Test
    void createUser_duplicateEmail() throws Exception {
        UserRequest req = new UserRequest();
        req.setName("test");
        req.setEmail("test@mail.com");
        req.setAge(30);

        when(mapper.toEntity(any())).thenReturn(new User("test", "test@mail.com", 30));
        doThrow(new DuplicateEntityException("Пользователь с таким email уже существует"))
                .when(service).createUser(any());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_success() throws Exception {
        User user = new User("test", "test@mail.com", 30);
        user.setId(1L);

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("test");
        dto.setEmail("test@mail.com");
        dto.setAge(30);
        dto.setCreatedAt(LocalDateTime.now());

        when(service.getUserById(1L)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserById_notFound() throws Exception {
        when(service.getUserById(1L))
                .thenThrow(new EntityNotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_success() throws Exception {
        User u1 = new User("A", "a@mail.com", 20);
        u1.setId(1L);

        User u2 = new User("B", "b@mail.com", 25);
        u2.setId(2L);

        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setName("A");
        dto1.setEmail("a@mail.com");
        dto1.setAge(20);

        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setName("B");
        dto2.setEmail("b@mail.com");
        dto2.setAge(25);

        when(service.getAllUsers()).thenReturn(List.of(u1, u2));
        when(mapper.toDto(u1)).thenReturn(dto1);
        when(mapper.toDto(u2)).thenReturn(dto2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateUser_success() throws Exception {
        UserRequest req = new UserRequest();
        req.setName("New");
        req.setEmail("new@mail.com");
        req.setAge(40);

        User user = new User("Old", "old@mail.com", 20);
        user.setId(1L);

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("New");
        dto.setEmail("new@mail.com");
        dto.setAge(40);

        when(service.getUserById(1L)).thenReturn(user);
        when(mapper.toDto(any())).thenReturn(dto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@mail.com"));
    }

    @Test
    void updateUser_notFound() throws Exception {
        UserRequest req = new UserRequest();
        req.setName("New");
        req.setEmail("new@mail.com");
        req.setAge(40);

        when(service.getUserById(1L))
                .thenThrow(new EntityNotFoundException("Пользователь не найден"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_notFound() throws Exception {
        doThrow(new EntityNotFoundException("Пользователь не найден"))
                .when(service).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound());
    }
}