package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    @Override
    public Item create(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item updatedItem) {
        return items.put(updatedItem.getId(), updatedItem);
    }

    @Override
    public void delete(long id) {
        items.remove(id);
    }

    @Override
    public Item getById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getItemsByOwnerId(long ownerId) {
        return getAll().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByNameOrDescription(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return getAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<Item> getAll() {
        return new ArrayList<>(items.values());
    }
}
