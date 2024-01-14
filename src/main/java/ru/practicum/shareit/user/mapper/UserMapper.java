package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    public abstract UserDto toUserDto(User user);

    public abstract List<UserDto> toUserDto(List<User> user);

    public abstract User toUser(UserDto userDto);
}
