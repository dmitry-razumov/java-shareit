package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    @Override
    public Item create(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        log.info("создана item - {}", item);
        return item;
    }

    @Override
    public Item update(Item updatedItem) {
        log.info("обновлена item - {}", updatedItem);
        return items.put(updatedItem.getId(), updatedItem);
    }

    @Override
    public void delete(long id) {
        log.info("удалена item - {}", id);
        items.remove(id);
    }

    @Override
    public Item getById(long id) {
        Item item = items.get(id);
        log.info("получена item - {} {}", id, item);
        return item;
    }

    @Override
    public List<Item> getItemsByOwnerId(long ownerId) {
        List<Item> itemList = getAll().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
        log.info("получены все вещи для ownerId={} {}", ownerId, itemList);
        return itemList;
    }

    @Override
    public List<Item> getItemsByNameOrDescription(String text) {
        if (text.isEmpty()) {
            log.info("вещей для nameOrdescription={} нет", text);
            return Collections.emptyList();
        }
        List<Item> itemList = getAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        log.info("получены все вещи для nameOrdescription={} {}", text, itemList);
        return itemList;
    }

    private List<Item> getAll() {
        return new ArrayList<>(items.values());
    }
}
