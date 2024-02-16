package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    static User createUser() {
        return User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.com")
                .build();
    }

    static UserDto createDto() {
        return UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@mail.com")
                .build();
    }

    @Test
    @SneakyThrows
    void shouldCreateUser() {
        UserDto userDto = createDto();
        User user = createUser();
        when(userService.create(any()))
                .thenReturn(user);
        String json = mapper.writeValueAsString(userDto);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.name").value(user.getName())
                );
    }

    @Test
    @SneakyThrows
    void shouldUpdateUser() {
        UserDto userDto = createDto();
        User user = createUser();
        when(userService.update(any()))
                .thenReturn(user);
        String json = mapper.writeValueAsString(userDto);
        mockMvc.perform(patch("/users/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.name").value(user.getName())
                );
    }

    @Test
    void shouldDeleteUser() {
        User user = createUser();
        userService.create(user);
        userService.delete(user.getId());
        verify(userService, times(1)).delete(user.getId());
    }

    @Test
    @SneakyThrows
    void shouldGetUserById() {
        User user = createUser();
        when(userService.getById(anyLong()))
                .thenReturn(user);
        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.name").value(user.getName())
                );
    }

    @Test
    @SneakyThrows
    void shouldGetAllUsers() {
        User user = createUser();
        when(userService.getAll())
                .thenReturn(List.of(user));
        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$[0].id").value(user.getId()),
                        jsonPath("$[0].name").value(user.getName())
                );
    }
}
