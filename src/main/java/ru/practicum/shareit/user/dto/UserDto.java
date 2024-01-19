package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.interfaces.CreateUser;
import ru.practicum.shareit.user.interfaces.UpdateUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@Builder
public class UserDto {
    private long id;
    @NotBlank(groups = {CreateUser.class},
            message = "name не должно быть пробелом, пустым или null")
    private String name;
    @Email(groups = {CreateUser.class, UpdateUser.class},
            message = "недопустимый формат email")
    @NotBlank(groups = {CreateUser.class},
            message = "email не должно быть пробелом, пустым или null")
    private String email;
}
