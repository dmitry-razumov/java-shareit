package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final UserMapper mapper;

    @GetMapping
    public List<UserDto> getAll() {
        return mapper.toUserDto(service.getAll());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        return mapper.toUserDto(service.getById(id));
    }

    @PostMapping
    public UserDto create(@Validated(UserDto.CreateUser.class)
                              @RequestBody UserDto userDto) {
        return mapper.toUserDto(service.create(mapper.toUser(userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto update(@Validated(UserDto.UpdateUser.class)
                              @PathVariable long id,
                              @RequestBody UserDto userDto) {
        userDto.setId(id);
        return mapper.toUserDto(service.update(mapper.toUser(userDto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
