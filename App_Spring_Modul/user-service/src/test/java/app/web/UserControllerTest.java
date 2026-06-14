package app.web;

import app.dto.UserDto;
import app.dto.UserRequest;
import app.hateoas.UserModelAssembler;
import app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import app.controller.UserController;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class,
                org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.class
        }
)
@Import({UserModelAssembler.class, UserControllerTest.HalConfig.class})
class UserControllerTest {

    @TestConfiguration
    static class HalConfig {
        @Bean
        Jackson2HalModule jackson2HalModule() {
            return new Jackson2HalModule();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_success() throws Exception {
        UserRequest req = new UserRequest("test", "test@mail.com", 30);
        UserDto dto = new UserDto(1L, "test", "test@mail.com", 30, LocalDateTime.now());

        when(service.createUser(any())).thenReturn(dto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@mail.com"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.all-users.href").exists());
    }

    @Test
    void getUserById_success() throws Exception {
        UserDto dto = new UserDto(1L, "test", "test@mail.com", 30, LocalDateTime.now());

        when(service.getUserById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.all-users.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists())
                .andExpect(jsonPath("$._links.update.href").exists());
    }

    @Test
    void getAllUsers_success() throws Exception {
        UserDto dto1 = new UserDto(1L, "A", "a@mail.com", 20, LocalDateTime.now());
        UserDto dto2 = new UserDto(2L, "B", "b@mail.com", 25, LocalDateTime.now());

        when(service.getAllUsers()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/users")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userDtoList.length()").value(2))
                .andExpect(jsonPath("$._embedded.userDtoList[0].name").value("A"))
                .andExpect(jsonPath("$._embedded.userDtoList[1].name").value("B"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void updateUser_success() throws Exception {
        UserRequest req = new UserRequest("New", "new@mail.com", 40);
        UserDto dto = new UserDto(1L, "New", "new@mail.com", 40, LocalDateTime.now());

        when(service.updateUser(1L, req)).thenReturn(dto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@mail.com"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}