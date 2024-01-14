package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item create(Item item, long userId) {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new NotFoundException("Собственник вещи не найден!");
        }
        item.setOwner(user);
        return itemStorage.create(item);
    }

    @Override
    public Item update(Item item, long userId) {
        Item updatedItem = itemStorage.getById(item.getId());
        if (updatedItem.getOwner().getId() != userId) {
            throw new NotFoundException("Собственник вещи не совпадает");
        }
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return itemStorage.update(updatedItem);
    }

    @Override
    public void delete(long id) {
        itemStorage.delete(id);
    }

    @Override
    public Item getById(long id) {
        return itemStorage.getById(id);
    }

    @Override
    public List<Item> getItemsByOwnerId(long ownerId) {
        return itemStorage.getItemsByOwnerId(ownerId);
    }

    @Override
    public List<Item> getItemsByNameOrDescription(String text) {
        return itemStorage.getItemsByNameOrDescription(text);
    }
}
