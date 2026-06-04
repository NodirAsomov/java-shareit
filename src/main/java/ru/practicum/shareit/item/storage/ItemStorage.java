package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;

import java.util.Map;

@Repository
public class ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    public Item create(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public Item get(Long id) {
        return items.get(id);
    }

    public Collection<Item> getAll() {
        return items.values();
    }
}