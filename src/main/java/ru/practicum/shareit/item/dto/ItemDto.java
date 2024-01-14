package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.request.ItemRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class ItemDto {
    private long id;
    @NotBlank(message = "name не должен быть пробелом, пустым или null")
    private String name;
    @NotNull(message = "description не должен быть null")
    private String description;
    @NotNull(message = "available не должен быть null")
    private Boolean available;
    private ItemRequest request;
}
