package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    private User user1;
    private User user2;
    private User updatedUser;

    @BeforeEach
    void beforeEach() {
       user1 = User.builder()
               .id(1L)
               .name("userName1")
               .email("user1@user.com")
               .build();
       user2 = User.builder()
               .id(2L)
               .name("userName2")
               .email("user2@user.com")
               .build();
       updatedUser = User.builder()
               .id(1L)
               .name("updatedUserName")
               .email("update@user.com")
               .build();
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.save(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        User resultUser = userService.create(user1);
        assertThat(user1.getId(), equalTo(resultUser.getId()));
    }

    @Test
    void shouldThrowIfUserNotExists() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getById(1L)
        );
        assertThat(exception.getMessage(), equalTo("Пользователь с id=1 не найден"));
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        User resultUser = userService.getById(1L);
        assertThat(1L, equalTo(resultUser.getId()));
    }

    @Test
    void shouldGetAllUsers() {
        List<User> users = List.of(user1, user2);
        when(userRepository.findAll()).thenReturn(users);
        List<User> userList = userService.getAll();
        assertThat(userList.size(), equalTo(2));
    }

    @Test
    void shouldGetEmptyByAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<User> userList = userService.getAll();
        assertThat(userList.size(), equalTo(0));
    }

    @Test
    void shouldUpdateUser() {
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        User resultUser = userService.update(updatedUser);
        assertThat(updatedUser.getName(), equalTo(resultUser.getName()));
        assertThat(updatedUser.getEmail(), equalTo(resultUser.getEmail()));
    }

    @Test
    void shouldThrowOnUpdateUserWhenWrongEmail() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.update(new User(1L, "updatedName", ""))
        );
        assertThat(exception.getMessage(), equalTo("email не должно быть пробелом или пустым"));
    }

    @Test
    void shouldThrowOnUpdateUserWhenWrongName() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.update(new User(1L, "", "update@mail.com"))
        );
        assertThat(exception.getMessage(), equalTo("name не должно быть пробелом или пустым"));
    }

    @Test
    void shouldThrowOnUpdateUserWhenNoUserExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.update(new User(100L, "userName", "user@mail.com"))
        );
        assertThat(exception.getMessage(), equalTo("Пользователь с id=100 не найден"));
    }

    @Test
    void shouldDeleteUser() {
        userService.create(user1);
        userService.delete(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
