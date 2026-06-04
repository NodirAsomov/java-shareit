package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(Long userId, ItemDto dto) {

        if (userStorage.get(userId) == null) {
            throw new NotFoundException("User not found");
        }

        Item i = new Item();
        i.setName(dto.getName());
        i.setDescription(dto.getDescription());
        i.setAvailable(dto.getAvailable());
        i.setOwnerId(userId);

        return ItemMapper.toDto(storage.create(i));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto dto) {

        Item i = storage.get(itemId);
        if (i == null) throw new NotFoundException("Item not found");

        if (!i.getOwnerId().equals(userId)) {
            throw new NotFoundException("Not owner");
        }

        if (dto.getName() != null) i.setName(dto.getName());
        if (dto.getDescription() != null) i.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) i.setAvailable(dto.getAvailable());

        return ItemMapper.toDto(storage.update(i));
    }

    @Override
    public ItemDto get(Long id) {
        Item i = storage.get(id);
        if (i == null) throw new NotFoundException("Item not found");
        return ItemMapper.toDto(i);
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {
        return storage.getAll().stream()
                .filter(i -> i.getOwnerId().equals(userId))
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        String q = text.toLowerCase();

        return storage.getAll().stream()
                .filter(Item::getAvailable)
                .filter(i ->
                        (i.getName() != null && i.getName().toLowerCase().contains(q)) ||
                                (i.getDescription() != null && i.getDescription().toLowerCase().contains(q))
                )
                .map(ItemMapper::toDto)
                .toList();
    }
}