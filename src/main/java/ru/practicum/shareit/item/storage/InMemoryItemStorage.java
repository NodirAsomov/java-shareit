package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> storage = new HashMap<>();
    private long id = 1;

    @Override
    public Item add(Item item) {
        item.setId(id++);
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(Long id) {
        return storage.get(id);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Item> getByOwnerId(Long ownerId) {
        return storage.values()
                .stream()
                .filter(item -> item.getOwner() != null)
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .toList();
    }
}
