package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemInRequestField;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import java.util.List;

@Mapper (componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    List<ItemRequestDto> toItemRequestDto(List<ItemRequest> itemRequest);

    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    @Mapping(target = "requestId", source = "request.id")
    ItemInRequestField toItemInRequestField(Item item);
}
