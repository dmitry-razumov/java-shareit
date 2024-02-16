package ru.practicum.shareit.user;

import org.springframework.context.annotation.ComponentScan;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.CreateUser;
import ru.practicum.shareit.user.interfaces.UpdateUser;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = UserMapper.class)
public class UserController {
    private final UserService service;
    private final UserMapper mapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAll() {
        log.info("GET /users");
        return mapper.toUserDto(service.getAll());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getById(@PathVariable long id) {
        log.info("GET /users/{{}}", id);
        return mapper.toUserDto(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated(CreateUser.class)
                              @RequestBody UserDto userDto) {
        log.info("POST /users with body {}", userDto);
        return mapper.toUserDto(service.create(mapper.toUser(userDto)));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@PathVariable long id,
                          @Validated(UpdateUser.class)
                          @RequestBody UserDto userDto) {
        log.info("PATCH /users/{{}} with body {}", id, userDto);
        userDto.setId(id);
        return mapper.toUserDto(service.update(mapper.toUser(userDto)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        log.info("DELETE /users/{{}}", id);
        service.delete(id);
    }
}
