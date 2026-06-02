package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item add(Item item);

    Item update(Item item);

    Item getById(Long id);

    List<Item> getAll();

    List<Item> getByOwnerId(Long ownerId);
}