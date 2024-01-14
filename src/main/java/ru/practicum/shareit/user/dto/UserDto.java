package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class UserDto {
    private long id;
    @NotBlank(groups = {CreateUser.class},
            message = "name не должен быть пробелом, пустым или null")
    private String name;
    @Email(groups = {CreateUser.class, UpdateUser.class},
            message = "недопустимый формат email")
    @NotBlank(groups = {CreateUser.class},
            message = "email не должен быть пробелом, пустым или null")
    private String email;

    public interface CreateUser {
        // marker interface for validation
    }

    public interface UpdateUser {
        // marker interface for validation
    }
}
