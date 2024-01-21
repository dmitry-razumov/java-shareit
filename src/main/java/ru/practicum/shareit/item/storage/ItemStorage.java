package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item updatedItem);

    void delete(long id);

    Item getById(long id);

    List<Item> getItemsByOwnerId(long ownerId);

    List<Item> getItemsByNameOrDescription(String text);
}
