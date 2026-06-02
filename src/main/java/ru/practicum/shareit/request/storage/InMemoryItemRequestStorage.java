package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.*;

@Repository
public class InMemoryItemRequestStorage implements ItemRequestStorage {

    private final Map<Long, ItemRequest> storage = new HashMap<>();
    private long id = 1;

    @Override
    public ItemRequest add(ItemRequest request) {
        request.setId(id++);
        storage.put(request.getId(), request);
        return request;
    }

    @Override
    public ItemRequest getById(Long id) {
        return storage.get(id);
    }

    @Override
    public List<ItemRequest> getAll() {
        return new ArrayList<>(storage.values());
    }
}