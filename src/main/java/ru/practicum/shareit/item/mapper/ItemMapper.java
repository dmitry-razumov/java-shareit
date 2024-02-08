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
    ItemDto toItemDto(Item item);

    List<ItemDto> toItemDto(List<Item> item);

    Item toItem(ItemDto itemDto);

//    @Mapping(target = "item.id", source = "item.id")
//    @Mapping(target = "item.name", source = "item.name")
    @Mapping(target = "authorName", source = "author.name")
    CommentDto commentToDto(Comment comment);

    List<CommentDto> commentToDto(List<Comment> comment);

    Comment dtoToComment(CommentDto commentDto);
}