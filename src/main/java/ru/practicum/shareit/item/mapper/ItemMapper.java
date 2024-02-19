package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "lastBooking.bookerId", source = "lastBooking.booker.id")
    @Mapping(target = "nextBooking.bookerId", source = "nextBooking.booker.id")
    @Mapping(target = "requestId", source = "request.id")
    ItemDto toItemDto(Item item);

    List<ItemDto> toItemDto(List<Item> item);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item toItem(ItemDto itemDto);

    @Mapping(target = "authorName", source = "author.name")
    CommentDto commentToDto(Comment comment);

    List<CommentDto> commentToDto(List<Comment> comment);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "item", ignore = true)
    Comment dtoToComment(CommentDto commentDto);
}