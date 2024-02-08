package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.interfaces.CreateItem;
import ru.practicum.shareit.request.ItemRequest;
import lombok.ToString;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class ItemDto {
    private long id;
    @NotBlank(groups = {CreateItem.class},
            message = "name не должно быть пробелом, пустым или null")
    private String name;
    @NotBlank(groups = {CreateItem.class},
            message = "description не должно быть пробелом, пустым или null")
    private String description;
    @NotNull(groups = {CreateItem.class},
            message = "available не должно быть null")
    private Boolean available;
    private ItemRequest request;
    private BookingInItemField lastBooking;
    private BookingInItemField nextBooking;
    private List<CommentDto> comments;
}
