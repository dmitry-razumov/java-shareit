package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toItemDto(Item item);

    List<ItemDto> toItemDto(List<Item> item);

    Item toItem(ItemDto itemDto);
}