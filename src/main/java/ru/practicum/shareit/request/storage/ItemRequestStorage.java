package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestStorage {

    ItemRequest add(ItemRequest request);

    ItemRequest getById(Long id);

    List<ItemRequest> getAll();
}