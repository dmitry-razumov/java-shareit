package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {
    public abstract ItemDto toItemDto(Item item);

    public abstract List<ItemDto> toItemDto(List<Item> item);

    public abstract Item toItem(ItemDto itemDto);
}
