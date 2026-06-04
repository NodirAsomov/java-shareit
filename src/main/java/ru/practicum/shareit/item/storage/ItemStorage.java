package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;

import java.util.Map;

@Repository
public class ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    public Item create(Item i) {
        i.setId(id++);
        items.put(i.getId(), i);
        return i;
    }

    public Item get(Long id) {
        return items.get(id);
    }

    public Collection<Item> getAll() {
        return items.values();
    }

    public Item update(Item i) {
        items.put(i.getId(), i);
        return i;
    }
}