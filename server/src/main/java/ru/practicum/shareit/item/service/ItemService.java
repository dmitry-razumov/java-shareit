package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemService {
    Item create(Item item, long userId, Long requestId);

    Item update(Item item, long userId);

    void delete(long id);

    Item getById(long id, long userId);

    List<Item> getItemsByOwnerId(long ownerId, int from, int size);

    List<Item> getItemsByNameOrDescription(String text, int from, int size);

    Comment addComment(long bookerId, long itemId, Comment comment);
}
