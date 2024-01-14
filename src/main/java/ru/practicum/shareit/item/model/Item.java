package ru.practicum.shareit.item.model;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.ItemRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
