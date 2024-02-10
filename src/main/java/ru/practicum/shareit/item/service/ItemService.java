package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemService {
    Item create(Item item, long userId);

    Item update(Item item, long userId);

    void delete(long id);

    Item getById(long id, long userId);

    List<Item> getItemsByOwnerId(long ownerId);

    List<Item> getItemsByNameOrDescription(String text);

    Comment addComment(long bookerId, long itemId, Comment comment);
}
