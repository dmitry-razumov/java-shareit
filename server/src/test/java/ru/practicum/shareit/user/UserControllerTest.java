package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
    private UserDto userDto;
    private User user;

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

    @BeforeEach
    void beforeEach() {
        userDto = createDto();
        user = createUser();
    }

    @Test
    void shouldCreateUser() throws Exception {
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
    void shouldUpdateUser() throws Exception {
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
        userService.create(user);
        userService.delete(user.getId());
        verify(userService, times(1)).delete(user.getId());
    }

    @Test
    void shouldGetUserById() throws Exception {
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
    void shouldGetAllUsers() throws Exception {
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
